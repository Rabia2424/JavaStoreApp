package com.store.storeapp.Services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(
            @Value("${app.upload.root:uploads}") String rootDir
    ) throws IOException {
        this.rootLocation = Paths.get(rootDir).toAbsolutePath().normalize();
        Files.createDirectories(this.rootLocation);
    }

    public String store(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String original = file.getOriginalFilename();
            String ext = "";

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }

            String fileName = UUID.randomUUID() + ext;
            Path dir = rootLocation.resolve(subDir).normalize();
            Files.createDirectories(dir);

            Path target = dir.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}

