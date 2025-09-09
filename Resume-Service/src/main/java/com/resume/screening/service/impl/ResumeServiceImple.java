package com.resume.screening.service.impl;

import com.resume.screening.config.Status;
import com.resume.screening.entity.Resume;
import com.resume.screening.repository.ResumeRepository;
import com.resume.screening.service.ResumeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class ResumeServiceImple implements ResumeService {

    private final ResumeRepository resumeRepository;

    private final FileStorageServiceImpl fileStorageService;

    private final TikaParsingService tikaParsingService;

    private final Executor parserExecutor;

    // private final RestTemplate restTemplate;  // Service communication


    @Override
    public Resume upload(MultipartFile file, Long userId, String requestId) throws IOException {
        // validation
        String orig = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(orig);
        String destFileName = UUID.randomUUID() + "-" + Instant.now().toEpochMilli() + "." + ext;

        // store file
        String storagePath = fileStorageService.store(file, destFileName);

        Resume r = Resume.builder()
                .userId(userId)
                .fileName(orig)
                .storagePath(storagePath)
                .uploadedAt(Instant.now())
                .status(Status.PENDING)
                .build();

        r = resumeRepository.save(r);

        // async parse
        Resume finalR = r;
        Resume finalR1 = r;
        CompletableFuture.runAsync(() -> parseAndPersist(finalR.getId()), parserExecutor)
                .exceptionally(ex -> {
                    // logging + set FAILED status
                    Optional<Resume> rr = resumeRepository.findById(finalR1.getId());
                    rr.ifPresent(res -> {
                        res.setStatus(Status.FAILED);
                        resumeRepository.save(res);
                    });
                    return null;
                });
        return r;
    }

    @Transactional
    @Override
    public void parseAndPersist(Long resumeId) {
        Resume r = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalStateException("Resume not found"));

        r.setStatus(Status.PARSING);
        resumeRepository.save(r);

        try (InputStream is = fileStorageService.get(r.getStoragePath())) {
            String parsed = tikaParsingService.parse(is, null);
            r.setParsedText(parsed);
            r.setParsedTime(Instant.now());
            r.setStatus(Status.PARSED);
            resumeRepository.save(r);

            // OPTIONAL: send message to AI service (via REST/Feign/Kafka)
            // callAiService(r);
        } catch (Exception e) {
            r.setStatus(Status.FAILED);
            resumeRepository.save(r);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resume getResume(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));
    }
}
