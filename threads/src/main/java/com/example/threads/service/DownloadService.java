package com.example.threads.service;

import java.util.List;

public interface DownloadService {

    List<DownloadInfo> getDownloadInfos(long packageId);
}