package com.example.threads.service;

public class DownloadServiceImpl {

    public void download(String url)
            throws Exception {

        System.out.println("Downloading from: " + url);

        Thread.sleep(2000);

        System.out.println("Download finished");
    }
}
