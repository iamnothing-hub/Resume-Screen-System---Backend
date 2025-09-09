package com.resume.screening.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public interface FileStorageService {

    String store(MultipartFile file, String destFilename) throws IOException;

    InputStream get(String storagePath) throws IOException;

    URL generateDownloadURL(String storagePath, Duration duration) throws MalformedURLException;
}
