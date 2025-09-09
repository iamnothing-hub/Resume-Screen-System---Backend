package com.resume.screening.controller;

import com.resume.screening.dto.AnalysisRequest;
import com.resume.screening.dto.AnalysisResponse;
import com.resume.screening.entity.Analysis;
import com.resume.screening.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AIService aiService;

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyzeResume(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(aiService.analyzeResume(request));
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<List<Analysis>> getAnalysisByResume(@PathVariable Long resumeId) {
        return ResponseEntity.ok(aiService.getAnalysisByResumeId(resumeId));
    }
}

