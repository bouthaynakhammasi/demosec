package com.aziz.demosec.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory);
}
