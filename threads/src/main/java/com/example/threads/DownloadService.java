package com.example.threads;

import java.util.List;

public interface DownloadService {

    List<DownloadInfo> getDownloadInfos(long packageId);
}