package com.example.threads;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import com.example.threads.service.DownloadInfo;
import org.junit.jupiter.api.Test;

class FileValidatorTest {

    @Test
    void acceptsValidFilesAtSizeBoundaries() {
        assertDoesNotThrow(() -> FileValidator.validate(List.of(
                file("empty.bin", 0, "empty-key", "https://example.com/empty"),
                file("maximum.bin", 100 * 1024 * 1024, "maximum-key", "https://example.com/maximum"))));
    }

    @Test
    void acceptsAnEmptyFileList() {
        assertDoesNotThrow(() -> FileValidator.validate(List.of()));
    }

    @Test
    void rejectsNullFileMetadata() {
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(Arrays.asList((DownloadInfo) null)));
    }

    @Test
    void rejectsBlankOrMissingFileNames() {
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file(null, 1, "key", "https://example.com/file"))));
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("  ", 1, "key", "https://example.com/file"))));
    }

    @Test
    void rejectsFileSizesOutsideAllowedRange() {
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("negative.bin", -1, "key", "https://example.com/file"))));
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(
                        file("too-large.bin", 100 * 1024 * 1024 + 1, "key", "https://example.com/file"))));
    }

    @Test
    void rejectsDuplicateFileNamesIgnoringCase() {
        assertThrows(IllegalArgumentException.class, () -> FileValidator.validate(List.of(
                file("report.txt", 1, "first-key", "https://example.com/first"),
                file("REPORT.TXT", 1, "second-key", "https://example.com/second"))));
    }

    @Test
    void rejectsForbiddenExtensionsIgnoringCase() {
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("installer.ExE", 1, "key", "https://example.com/file"))));
    }

    @Test
    void rejectsMissingFileKeysAndDownloadUrls() {
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("file.bin", 1, null, "https://example.com/file"))));
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("file.bin", 1, " ", "https://example.com/file"))));
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("file.bin", 1, "key", null))));
        assertThrows(IllegalArgumentException.class,
                () -> FileValidator.validate(List.of(file("file.bin", 1, "key", " "))));
    }

    private DownloadInfo file(String name, int size, String key, String url) {
        return new TestDownloadInfo(name, size, key, url);
    }

    private record TestDownloadInfo(String originalFileName, int size, String fileKey, String downloadURL)
            implements DownloadInfo {

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public String getOriginalFileName() {
            return originalFileName;
        }

        @Override
        public String getFileKey() {
            return fileKey;
        }

        @Override
        public String getDownloadURL() {
            return downloadURL;
        }
    }
}
