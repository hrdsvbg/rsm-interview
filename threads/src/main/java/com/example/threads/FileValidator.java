package com.example.threads;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.threads.service.DownloadInfo;

public class FileValidator {

    private static final long MAX_SIZE = 100L * 1024 * 1024;

    private static final Set<String> EXCLUDED_FILE_TYPES = Set.of("cmd", "com", "dll", "dmg", "exe", "iso", "jar",
            "js");

    public static void validate(List<DownloadInfo> files) {

        Set<String> seen = new HashSet<>();

        for (DownloadInfo file : files) {

            if (file == null) {
                throw new IllegalArgumentException("Null file metadata");
            }

            String name = file.getOriginalFileName();

            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Invalid filename");
            }

            if (file.getSize() < 0 || file.getSize() > MAX_SIZE) {
                throw new IllegalArgumentException("Invalid size: " + name);
            }

            String lowerCaseName = name.toLowerCase();

            if (seen.contains(lowerCaseName)) {
                throw new IllegalArgumentException("Duplicate filename: " + name);
            } else {
                seen.add(lowerCaseName);
            }

            String fileExtension = "";
            int dot = name.lastIndexOf('.');

            if (dot >= 0) {
                fileExtension = lowerCaseName.substring(dot + 1);
            }

            if (EXCLUDED_FILE_TYPES.contains(fileExtension)) {
                throw new IllegalArgumentException("Forbidden file type: " + name);
            }

            if (file.getFileKey() == null || file.getFileKey().isBlank()) {
                throw new IllegalArgumentException("Invalid file key: " + name);
            }

            if (file.getDownloadURL() == null || file.getDownloadURL().isBlank()) {
                throw new IllegalArgumentException("Invalid url: " + name);
            }
        }
    }
}
