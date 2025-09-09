package com.resume.screening.controller;

import com.resume.screening.dto.ResumeResponseDto;
import com.resume.screening.entity.Resume;
import com.resume.screening.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // Expect Authorization: Bearer <token>
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value="X-Request-Id", required = false) String requestId) throws IOException {

        // userId can also be obtained from JWT claims in JwtAuthFilter and injected into SecurityContext
        Resume r = resumeService.upload(file, userId, requestId);

        Map<String,Object> body = Map.of("resumeId", r.getId(), "status", r.getStatus());
        return ResponseEntity.accepted().body(body); // 202 - processing
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponseDto> get(@PathVariable Long id) {
        Resume r = resumeService.getResume(id);
        ResumeResponseDto dto = ResumeResponseDto.from(r);
        return ResponseEntity.ok(dto);
    }
}
