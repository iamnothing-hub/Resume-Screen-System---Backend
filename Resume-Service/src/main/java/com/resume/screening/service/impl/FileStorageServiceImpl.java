package com.resume.screening.service.impl;

import com.resume.screening.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String storageLocation;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(storageLocation));
    }


    @Override
    public String store(MultipartFile file, String destFilename) throws IOException {
        Path path = Paths.get(storageLocation, destFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return path.toString();
    }

    @Override
    public InputStream get(String storagePath) throws IOException {
        return Files.newInputStream(Paths.get(storagePath));
    }

    @Override
    public URL generateDownloadURL(String storagePath, Duration duration) throws MalformedURLException {
        return Paths.get(storagePath).toUri().toURL();
    }
}
