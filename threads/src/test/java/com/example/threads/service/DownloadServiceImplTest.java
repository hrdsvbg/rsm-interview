package com.example.threads.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DownloadServiceImplTest {

    @TempDir
    Path tempDir;

    private HttpServer server;
    private final Map<String, Path> servedFiles = new HashMap<>();
    private final Map<String, byte[]> receivedUploads = new ConcurrentHashMap<>();

    @BeforeEach
    void startHttpServer() throws IOException {
        servedFiles.clear();
        servedFiles.put("served.bin", writeServedFile("served.bin", new byte[] { 0, 1, 2, 42, -1 }));
        servedFiles.put("second.bin", writeServedFile("second.bin", new byte[] { 9, 8, 7, 6 }));

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/", this::handleRequest);
        server.start();
    }

    @AfterEach
    void stopHttpServer() {
        server.stop(0);
    }

    @Test
    void downloadsActualFileFromHttpServer() throws Exception {
        Path destination = tempDir.resolve("downloaded.bin");
        String url = serverUrl("/download/served.bin");

        new DownloadServiceImpl().download(url, destination);

        assertArrayEquals(
                Files.readAllBytes(servedFiles.get("served.bin")),
                Files.readAllBytes(destination));
    }

    @Test
    void downloadsMultipleActualFilesFromHttpServer() throws Exception {
        DownloadServiceImpl downloader = new DownloadServiceImpl();

        for (Map.Entry<String, Path> servedFile : servedFiles.entrySet()) {
            Path destination = tempDir.resolve("downloaded-" + servedFile.getKey());
            downloader.download(serverUrl("/download/" + servedFile.getKey()), destination);

            assertArrayEquals(
                    Files.readAllBytes(servedFile.getValue()),
                    Files.readAllBytes(destination));
        }
    }

    @Test
    void httpServerAcceptsPostedData() throws Exception {
        byte[] upload = new byte[] { 3, 5, 8, 13 };
        HttpRequest request = HttpRequest.newBuilder(URI.create(serverUrl("/upload/file-key")))
                .POST(HttpRequest.BodyPublishers.ofByteArray(upload))
                .build();

        HttpResponse<Void> response = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.discarding());

        assertEquals(204, response.statusCode());
        assertArrayEquals(upload, receivedUploads.get("file-key"));
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("GET".equals(exchange.getRequestMethod()) && path.startsWith("/download/")) {
            Path file = servedFiles.get(path.substring("/download/".length()));
            if (file != null) {
                sendResponse(exchange, 200, Files.readAllBytes(file));
                return;
            }
        }

        if ("POST".equals(exchange.getRequestMethod()) && path.startsWith("/upload/")) {
            String key = path.substring("/upload/".length());
            receivedUploads.put(key, exchange.getRequestBody().readAllBytes());
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        sendResponse(exchange, 404, new byte[0]);
    }

    private void sendResponse(HttpExchange exchange, int status, byte[] body) throws IOException {
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private Path writeServedFile(String name, byte[] contents) throws IOException {
        Path file = tempDir.resolve(name);
        Files.write(file, contents);
        return file;
    }

    private String serverUrl(String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }
}
