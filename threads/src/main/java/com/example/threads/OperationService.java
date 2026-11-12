
package com.example.threads;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.example.threads.report.OperationReport;
import com.example.threads.service.DownloadInfo;
import com.example.threads.service.DownloadService;
import com.example.threads.service.UploadService;

public class OperationService {

    private static final int THREAD_COUNT = 5;
    private static final int MAX_CAPACITY = 100 * 1024 * 1024;

    private final DownloadService downloadService;
    private final UploadService uploadService;

    public OperationService(
            DownloadService downloadService,
            UploadService uploadService) {

        this.downloadService = downloadService;
        this.uploadService = uploadService;
    }

    public OperationReport processPackage(long packageId) {

        List<DownloadInfo> files = downloadService.getDownloadInfos(packageId);

        FileValidator.validate(files);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

        Semaphore semaphore = new Semaphore(MAX_CAPACITY);

        for (DownloadInfo file : files) {

            pool.submit(() -> {

                String thread = Thread.currentThread().getName();

                try {
                    System.out.println(
                            thread + " waiting for " +
                                    file.getOriginalFileName() +
                                    " (" + file.getSize() + " bytes)");

                    semaphore.acquire(file.getSize());

                    try {
                        System.out.println(
                                thread + " ACQUIRED permits for " +
                                        file.getOriginalFileName());

                        // Simulate work
                        Thread.sleep(3000);

                        System.out.println(
                                thread + " FINISHED working " +
                                        file.getOriginalFileName());

                    } finally {
                        semaphore.release(file.getSize());

                        System.out.println(
                                thread + " RELEASED permits for " +
                                        file.getOriginalFileName());
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        pool.shutdown();

        try {
            if (!pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                pool.shutdownNow();
                throw new IllegalStateException("Timed out while processing package " + packageId);
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while processing package " + packageId, e);
        }

        System.out.println("Work is done");
        return null;
    }
}