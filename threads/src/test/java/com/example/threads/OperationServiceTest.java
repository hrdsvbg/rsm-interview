package com.example.threads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.example.threads.report.OperationReport;
import com.example.threads.service.DownloadInfo;
import com.example.threads.service.DownloadService;
import com.example.threads.service.UploadService;
import com.example.threads.service.UploadServiceImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class OperationServiceTest {

    private static final int BUFFER_SIZE = 64 * 1024;

    private HttpServer server;
    private ExecutorService serverExecutor;
    private final Map<String, Payload> downloadableFiles = new ConcurrentHashMap<>();
    private final Map<String, UploadResult> uploadedFiles = new ConcurrentHashMap<>();
    private final AtomicLong activeDownloadBytes = new AtomicLong();
    private final AtomicLong maxConcurrentDownloadBytes = new AtomicLong();

    @BeforeEach
    void startHttpServer() throws IOException {
        downloadableFiles.clear();
        uploadedFiles.clear();
        activeDownloadBytes.set(0);
        maxConcurrentDownloadBytes.set(0);

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handleRequest);
        serverExecutor = Executors.newFixedThreadPool(8);
        server.setExecutor(serverExecutor);
        server.start();
    }

    @AfterEach
    void stopHttpServer() {
        server.stop(0);
        serverExecutor.shutdownNow();
    }

    @Test
    void processPackageDownloadsAndUploadsFilesAndReturnsReport() {
        Payload firstPayload = new Payload(8_193, (byte) 31);
        Payload secondPayload = new Payload(17_001, (byte) 87);
        downloadableFiles.put("first.bin", firstPayload);
        downloadableFiles.put("second.bin", secondPayload);

        DownloadService downloadService = mock(DownloadService.class);
        when(downloadService.getDownloadInfos(42L)).thenReturn(List.of(
                file("first.bin", firstPayload.size(), "first-key"),
                file("second.bin", secondPayload.size(), "second-key")));

        OperationService operationService = new OperationService(downloadService, new UploadServiceImpl());

        OperationReport report = operationService.processPackage(42L);

        assertEquals(2, report.getFiles().size());
        assertEquals(2, report.getSuccesses());
        assertEquals(0, report.getFailures());
        assertEquals(2, report.getFiles().stream().filter(file -> file.isSuccessful()).count());
        assertEquals("first.bin", report.getFiles().get(0).getName());
        assertEquals("second.bin", report.getFiles().get(1).getName());
        assertUploaded("first-key", firstPayload);
        assertUploaded("second-key", secondPayload);
        verify(downloadService).getDownloadInfos(42L);
    }

    @Test
    @Timeout(60)
    void processPackageLimitsConcurrentLargeDownloadsWithSemaphore() {
        int megabyte = 1024 * 1024;
        List<Payload> payloads = List.of(
                new Payload(50 * megabyte, (byte) 19),
                new Payload(60 * megabyte, (byte) 73),
                new Payload(20 * megabyte, (byte) 37),
                new Payload(20 * megabyte, (byte) 91),
                new Payload(10 * megabyte, (byte) 11));
        List<DownloadInfo> files = new ArrayList<>();
        for (int i = 0; i < payloads.size(); i++) {
            String name = "large-" + (i + 1) + ".bin";
            String key = "large-" + (i + 1) + "-key";
            Payload payload = payloads.get(i);
            downloadableFiles.put(name, payload);
            files.add(file(name, payload.size(), key));
        }

        DownloadService downloadService = mock(DownloadService.class);
        when(downloadService.getDownloadInfos(42L)).thenReturn(files);

        OperationService operationService = new OperationService(downloadService, new UploadServiceImpl());

        OperationReport report = operationService.processPackage(42L);

        assertEquals(5, report.getSuccesses());
        assertEquals(0, report.getFailures());
        assertTrue(maxConcurrentDownloadBytes.get() <= 100L * megabyte);
        assertTrue(maxConcurrentDownloadBytes.get() > 60L * megabyte);
        for (int i = 0; i < payloads.size(); i++) {
            assertUploaded("large-" + (i + 1) + "-key", payloads.get(i));
        }
    }

    @Test
    void processPackageRejectsInvalidDownloadedFiles() {
        DownloadService downloadService = mock(DownloadService.class);
        UploadService uploadService = mock(UploadService.class);
        DownloadInfo file = mock(DownloadInfo.class);
        when(downloadService.getDownloadInfos(42L)).thenReturn(List.of(file));
        when(file.getOriginalFileName()).thenReturn("");

        OperationService operationService = new OperationService(downloadService, uploadService);

        assertThrows(IllegalArgumentException.class, () -> operationService.processPackage(42L));
        verify(downloadService).getDownloadInfos(42L);
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("GET".equals(exchange.getRequestMethod()) && path.startsWith("/download/")) {
            String name = path.substring("/download/".length());
            Payload payload = downloadableFiles.get(name);
            if (payload != null) {
                sendDownload(exchange, payload);
                return;
            }
        }

        if ("POST".equals(exchange.getRequestMethod()) && path.startsWith("/upload/")) {
            String key = path.substring("/upload/".length());
            try (InputStream input = exchange.getRequestBody()) {
                MessageDigest digest = sha256();
                byte[] buffer = new byte[BUFFER_SIZE];
                long size = 0;
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                    size += bytesRead;
                }
                uploadedFiles.put(key, new UploadResult(size, HexFormat.of().formatHex(digest.digest())));
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(404, -1);
        exchange.close();
    }

    private void sendDownload(HttpExchange exchange, Payload payload) throws IOException {
        long activeBytes = activeDownloadBytes.addAndGet(payload.size());
        maxConcurrentDownloadBytes.accumulateAndGet(activeBytes, Math::max);
        try {
            exchange.sendResponseHeaders(200, payload.size());
            try (OutputStream output = exchange.getResponseBody()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int remaining = payload.size();
                while (remaining > 0) {
                    int chunkSize = Math.min(buffer.length, remaining);
                    for (int i = 0; i < chunkSize; i++) {
                        buffer[i] = payload.value();
                    }
                    output.write(buffer, 0, chunkSize);
                    remaining -= chunkSize;
                    if (payload.size() > BUFFER_SIZE) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Download response interrupted", e);
                        }
                    }
                }
            }
        } finally {
            activeDownloadBytes.addAndGet(-payload.size());
        }
    }

    private DownloadInfo file(String name, int size, String key) {
        return new TestDownloadInfo(
                name,
                size,
                serverUrl("/upload/" + key),
                serverUrl("/download/" + name));
    }

    private String serverUrl(String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }

    private void assertUploaded(String key, Payload expected) {
        UploadResult actual = uploadedFiles.get(key);
        assertTrue(actual != null, "No upload received for " + key);
        assertEquals(expected.size(), actual.size());
        assertEquals(hash(expected), actual.sha256());
    }

    private String hash(Payload payload) {
        MessageDigest digest = sha256();
        byte[] buffer = new byte[BUFFER_SIZE];
        java.util.Arrays.fill(buffer, payload.value());
        int remaining = payload.size();
        while (remaining > 0) {
            int chunkSize = Math.min(buffer.length, remaining);
            digest.update(buffer, 0, chunkSize);
            remaining -= chunkSize;
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private record Payload(int size, byte value) {
    }

    private record UploadResult(long size, String sha256) {
    }

    private static class TestDownloadInfo implements DownloadInfo {
        private final String originalFileName;
        private final int size;
        private final String fileKey;
        private final String downloadURL;

        private TestDownloadInfo(String originalFileName, int size, String fileKey, String downloadURL) {
            this.originalFileName = originalFileName;
            this.size = size;
            this.fileKey = fileKey;
            this.downloadURL = downloadURL;
        }

        @Override
        public String getOriginalFileName() {
            return originalFileName;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public String getFileKey() {
            return fileKey;
        }

        @Override
        public String getDownloadURL() {
            return downloadURL;
        }
    }
}
