package com.example.threads;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.threads.service.DownloadInfo;
import com.example.threads.service.DownloadService;
import com.example.threads.service.UploadService;

class OperationServiceTest {

    @Test
    void processPackageUsesSemaphoreForMultipleLargeFiles() {
        DownloadService downloadService = mock(DownloadService.class);
        UploadService uploadService = mock(UploadService.class);
        int fileSizeInMB = 1024 * 1024;
        List<DownloadInfo> files = List.of(
                validFile("first.txt", 50 * fileSizeInMB),
                validFile("second.txt", 80 * fileSizeInMB),
                validFile("third.txt", 30 * fileSizeInMB),
                validFile("fourth.txt", 20 * fileSizeInMB),
                validFile("fifth.txt", 20 * fileSizeInMB));
        when(downloadService.getDownloadInfos(42L)).thenReturn(files);

        OperationService operationService = new OperationService(downloadService, uploadService);

        assertDoesNotThrow(() -> operationService.processPackage(42L));
        verify(downloadService).getDownloadInfos(42L);
    }

    @Test
    void processPackageRejectsInvalidDownloadedFiles() {
        DownloadService downloadService = mock(DownloadService.class);
        UploadService uploadService = mock(UploadService.class);
        DownloadInfo file = mock(DownloadInfo.class);
        when(downloadService.getDownloadInfos(42L)).thenReturn(List.of(file));
        when(file.getOriginalFileName()).thenReturn("");

        OperationService operationService = new OperationService(downloadService, uploadService);

        assertThrows(IllegalArgumentException.class, () -> operationService.processPackage(42L));
        verify(downloadService).getDownloadInfos(42L);
    }

    private DownloadInfo validFile(String name, int size) {
        DownloadInfo file = mock(DownloadInfo.class);
        when(file.getOriginalFileName()).thenReturn(name);
        when(file.getSize()).thenReturn(size);
        when(file.getFileKey()).thenReturn(name + "-key");
        when(file.getDownloadURL()).thenReturn("https://example.com/" + name);
        return file;
    }
}
