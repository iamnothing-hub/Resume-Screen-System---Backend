package com.resume.screening.service;

import com.resume.screening.dto.AnalysisRequest;
import com.resume.screening.dto.AnalysisResponse;
import com.resume.screening.entity.Analysis;

import java.util.List;

public interface AIService {

    AnalysisResponse analyzeResume(AnalysisRequest request);

    List<Analysis> getAnalysisByResumeId(Long resumeId);
}
