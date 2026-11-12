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
    void processPackageLoadsAndValidatesDownloadedFiles() {
        DownloadService downloadService = mock(DownloadService.class);
        UploadService uploadService = mock(UploadService.class);
        DownloadInfo file = mock(DownloadInfo.class);
        when(downloadService.getDownloadInfos(42L)).thenReturn(List.of(file));
        when(file.getOriginalFileName()).thenReturn("document.txt");
        when(file.getSize()).thenReturn(1);
        when(file.getFileKey()).thenReturn("document-key");
        when(file.getDownloadURL()).thenReturn("https://example.com/document.txt");

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
}
