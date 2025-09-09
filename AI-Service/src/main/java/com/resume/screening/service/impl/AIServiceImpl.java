package com.resume.screening.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.screening.dto.AnalysisRequest;
import com.resume.screening.dto.AnalysisResponse;
import com.resume.screening.entity.Analysis;
import com.resume.screening.repository.AnalysisRepository;
import com.resume.screening.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final ChatClient chatClient;

    public AIServiceImpl( ChatClient.Builder chatClientBuilder, AnalysisRepository analysisRepository, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.analysisRepository = analysisRepository;
        this.objectMapper = objectMapper;
    }

    private final AnalysisRepository analysisRepository;

    private final ObjectMapper objectMapper;


    @Override
    public AnalysisResponse analyzeResume(AnalysisRequest request) {

        String prompt = """
                Analyze this resume and job description.
                
                Resume:
                %s
                
                Job Description:
                %s
                
                Respond in  Strictly JSON format and don't write ` symbol in the end with the following fields:
                {
                    "score: integer (0-100),
                    "matchedSkills": [list of strings],
                    "strengths": [list of strings],
                    "weaknesses": [list of strings],
                    "recommendations": [list of strings]
                }
                """.formatted(request.getResumeText(), request.getJobDescription());

        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        try {
            JsonNode jsonNode = objectMapper.readTree(aiResponse);

            Analysis analysis = Analysis.builder()
                    .resumeId(request.getResumeId())
                    .score(jsonNode.get("score").asInt())
                    .matchedSkills(jsonNode.get("matchedSkills").toString())
                    .strengths(jsonNode.get("strengths").toString())
                    .weaknesses(jsonNode.get("weaknesses").toString())
                    .recommendations(jsonNode.get("recommendations").toString())
                    .analyzedAt(Instant.now())
                    .build();

            analysisRepository.save(analysis);

            return new AnalysisResponse(
                    analysis.getScore(),
                    analysis.getMatchedSkills(),
                    analysis.getStrengths(),
                    analysis.getWeaknesses(),
                    analysis.getRecommendations(),
                    analysis.getAnalyzedAt()
            );
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response" + aiResponse, e);
        }

    }

    @Override
    public List<Analysis> getAnalysisByResumeId(Long resumeId) {
        return analysisRepository.findByResumeId(resumeId);
    }
}
