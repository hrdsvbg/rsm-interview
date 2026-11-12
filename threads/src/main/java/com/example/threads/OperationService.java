package com.example.threads;

import java.util.List;

import com.example.threads.service.DownloadInfo;
import com.example.threads.service.DownloadService;
import com.example.threads.service.UploadService;

public class OperationService {

    private final DownloadService downloadService;
    private final UploadService uploadService;

    public OperationService(
            DownloadService downloadService,
            UploadService uploadService){

        this.downloadService = downloadService;
        this.uploadService = uploadService;
    }

    public void processPackage(long packageId){
        List<DownloadInfo> files = downloadService.getDownloadInfos(packageId);
        FileValidator.validate(files);
    }

}