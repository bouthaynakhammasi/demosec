package com.aziz.demosec.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageServiceImpl fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl(tempDir.toString());
    }

    @Test
    void storeFile_ShouldSaveFileToSubdirectory() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );

        // Act
        String resultPath = fileStorageService.storeFile(file, "avatars");

        // Assert
        assertNotNull(resultPath);
        assertTrue(resultPath.startsWith("avatars/"));
        assertTrue(tempDir.resolve(resultPath).toFile().exists());
    }

    @Test
    void storeFile_ShouldThrowException_WhenInvalidFileName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "../evil.exe", "text/plain", "malicious".getBytes()
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(file, "uploads"));
    }
}
