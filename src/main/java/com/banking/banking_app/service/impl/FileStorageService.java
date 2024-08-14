package com.banking.banking_app.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    public String saveProfilePicture(MultipartFile profilePicFile, String uploadDir) {
        try {
            String picName = UUID.randomUUID().toString() + "_" + profilePicFile.getOriginalFilename();
            Path profilePicPath = Paths.get(uploadDir, picName);
            Files.copy(profilePicFile.getInputStream(), profilePicPath);
            return profilePicPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store profile picture", e);
        }
    }
}
