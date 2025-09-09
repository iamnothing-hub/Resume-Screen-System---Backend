package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalysisResponse {
    private int score;
    private String matchedSkills;
    private String strengths;
    private String weaknesses;
    private String recommendations;
    private Instant analyzedAt;
}
