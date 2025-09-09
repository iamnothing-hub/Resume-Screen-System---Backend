package com.resume.screening.repository;

import com.resume.screening.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByResumeId(Long resumeId);
}

