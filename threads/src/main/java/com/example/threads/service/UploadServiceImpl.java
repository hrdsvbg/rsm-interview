package com.example.threads.service;

import java.io.InputStream;

public class UploadServiceImpl implements UploadService {

    public void doUpload(String key, InputStream data, int size)
            throws Exception {

        System.out.println("Uploading: " + key);

        Thread.sleep(2000);

        System.out.println("Upload finished: " + key);
    }
}
