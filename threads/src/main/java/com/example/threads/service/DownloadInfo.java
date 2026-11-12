package com.example.threads;

public interface DownloadInfo {

    int getSize();

    String getOriginalFileName();

    String getFileKey();

    String getDownloadURL();
}
