package com.example.threads.service;

public interface DownloadInfo {

    int getSize();

    String getOriginalFileName();

    String getFileKey();

    String getDownloadURL();
}
