
package com.example.threads;

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

        long startTime = System.currentTimeMillis();

        List<DownloadInfo> files = downloadService.getDownloadInfos(packageId);

        FileValidator.validate(files);

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

        Semaphore semaphore = new Semaphore(MAX_CAPACITY);

        List<Future<FileReport>> futures = new ArrayList<>();

        for (DownloadInfo file : files) {

            Callable<FileReport> task = () -> {

                String thread = Thread.currentThread().getName();

                boolean success = true;
                long uploadTime = 0;

                try {
                    System.out.println(
                            thread + " waiting for "
                                    + file.getOriginalFileName()
                                    + " (" + file.getSize() + " bytes)");

                    semaphore.acquire(file.getSize());

                    try {
                        System.out.println(
                                thread + " STARTED DOWNLOADING "
                                        + file.getOriginalFileName());

                        Thread.sleep(3000);

                        System.out.println(
                                thread + " FINISHED DOWNLOADING "
                                        + file.getOriginalFileName());

                    } finally {
                        semaphore.release(file.getSize());

                        System.out.println(
                                thread + " RELEASED CAPACITY for "
                                        + file.getOriginalFileName());
                    }

                    long uploadStart = System.currentTimeMillis();

                    System.out.println(
                            thread + " STARTED UPLOADING "
                                    + file.getOriginalFileName());

                    Thread.sleep(2000);

                    uploadTime = System.currentTimeMillis() - uploadStart;

                    System.out.println(
                            thread + " FINISHED UPLOADING "
                                    + file.getOriginalFileName());

                } catch (InterruptedException e) {

                    success = false;
                    Thread.currentThread().interrupt();

                    System.out.println(
                            thread + " INTERRUPTED "
                                    + file.getOriginalFileName());
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

        System.out.println("Work is done");
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Successes: " + successes);
        System.out.println("Failures: " + failures);
        System.out.println("");

        return new OperationReport(
                reports,
                totalTime,
                successes,
                failures);
    }
}
