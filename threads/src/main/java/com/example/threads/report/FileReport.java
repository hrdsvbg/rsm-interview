package com.example.threads.report;

public class FileReport {

    private final String name;
    private final long uploadTime;
    private final boolean successful;

    public FileReport(
            String name,
            long uploadTime,
            boolean success) {

        this.name = name;
        this.uploadTime = uploadTime;
        this.successful = success;
    }

    public String getName() {
        return name;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
