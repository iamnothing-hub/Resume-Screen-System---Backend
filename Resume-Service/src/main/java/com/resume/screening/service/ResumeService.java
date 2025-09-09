package com.resume.screening.service;

import com.resume.screening.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {

    Resume upload(MultipartFile file, Long userId, String requestId) throws IOException;

    void parseAndPersist(Long resumeId);

    Resume getResume(Long id);

}
