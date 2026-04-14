package com.aziz.demosec.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final String uploadDir = "uploads/prescriptions";

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            // Create directory if not exists
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = root.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Return the URL (assuming the app runs on localhost:8081)
            // In a real app, this should be a relative path or a proper domain
            String fileUrl = "/uploads/prescriptions/" + filename;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }
}
