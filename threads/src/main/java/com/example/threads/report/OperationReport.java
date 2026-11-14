package com.example.threads.report;

import java.util.List;

public class OperationReport {

    private final List<FileReport> files;
    private final long totalTime;
    private final int successes;
    private final int failures;

    public OperationReport(
            List<FileReport> files,
            long totalTime,
            int successes,
            int failures) {

        this.files = files;
        this.totalTime = totalTime;
        this.successes = successes;
        this.failures = failures;
    }

    public List<FileReport> getFiles() {
        return files;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getFailures() {
        return failures;
    }
}
