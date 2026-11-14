
package com.example.threads;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.example.threads.report.FileReport;
import com.example.threads.report.OperationReport;
import com.example.threads.service.DownloadInfo;
import com.example.threads.service.DownloadService;
import com.example.threads.service.DownloadServiceImpl;
import com.example.threads.service.UploadService;

public class OperationService {

    private static final int THREAD_COUNT = 5;
    private static final int MAX_CAPACITY = 100 * 1024 * 1024;

    private final DownloadService downloadService;
    private final DownloadServiceImpl downloader;
    private final UploadService uploadService;

    public OperationService(
            DownloadService downloadService,
            UploadService uploadService) {

        this.downloadService = downloadService;
        this.downloader = new DownloadServiceImpl();
        this.uploadService = uploadService;
    }

    public OperationReport processPackage(long packageId) {

        long startTime = System.currentTimeMillis();

        List<DownloadInfo> files = downloadService.getDownloadInfos(packageId);

        FileValidator.validate(files);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

        Semaphore semaphore = new Semaphore(MAX_CAPACITY);

        List<Future<FileReport>> futures = new ArrayList<>();

        for (DownloadInfo file : files) {

            Callable<FileReport> task = () -> {

                boolean success = true;
                long uploadTime = 0;
                Path tempFile = null;

                try {
                    semaphore.acquire(file.getSize());

                    try {
                        tempFile = Files.createTempFile(file.getOriginalFileName(), ".tmp");
                        downloader.download(file.getDownloadURL(), tempFile);

                    } finally {
                        semaphore.release(file.getSize());
                    }
                    long uploadStart = System.currentTimeMillis();

                    try (InputStream inputStream = Files.newInputStream(tempFile)) {
                        uploadService.doUpload(
                                file.getFileKey(),
                                inputStream,
                                file.getSize());
                    }

                    uploadTime = System.currentTimeMillis() - uploadStart;

                } catch (InterruptedException e) {
                    success = false;
                    Thread.currentThread().interrupt();
                }

                if (tempFile != null) {
                    Files.delete(tempFile);
                }

                return new FileReport(
                        file.getOriginalFileName(),
                        uploadTime,
                        success);
            };

            futures.add(pool.submit(task));
        }

        pool.shutdown();

        List<FileReport> reports = new ArrayList<>();

        int successes = 0;
        int failures = 0;

        try {
            for (Future<FileReport> future : futures) {

                FileReport report = future.get();

                reports.add(report);

                if (report.isSuccessful()) {
                    successes++;
                } else {
                    failures++;
                }
            }

            pool.awaitTermination(
                    Long.MAX_VALUE,
                    TimeUnit.NANOSECONDS);

        } catch (Exception e) {

            pool.shutdownNow();

            throw new IllegalStateException(
                    "Error processing package " + packageId, e);
        }

        long totalTime = System.currentTimeMillis() - startTime;

        return new OperationReport(
                reports,
                totalTime,
                successes,
                failures);
    }
}
