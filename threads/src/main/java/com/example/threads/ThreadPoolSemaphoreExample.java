package com.example.threads;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ThreadPoolSemaphoreExample {

    public static void main(String[] args) {

        // Exactly 5 worker threads
        ExecutorService pool =
                Executors.newFixedThreadPool(5);

        // Only 2 permits
        Semaphore semaphore =
                new Semaphore(2);

        for (int i = 1; i <= 10; i++) {

            int taskId = i;

            pool.submit(() -> {

                try {

                    System.out.println(
                            "Task " + taskId +
                            " waiting for permission..."
                    );

                    // Wait here if no permit is available
                    semaphore.acquire();

                    try {

                        // CONDITION IS SATISFIED
                        // We have a permit.

                        System.out.println(
                                "Task " + taskId +
                                " STARTED on " +
                                Thread.currentThread().getName()
                        );

                        // Simulate work
                        Thread.sleep(3000);

                        System.out.println(
                                "Task " + taskId +
                                " FINISHED"
                        );

                    } finally {

                        // Give permit back
                        semaphore.release();
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            });
        }

        pool.shutdown();
    }
}