package com.resume.screening.dto;

import com.resume.screening.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponseDto {
    private Long id;
    private Long userId;
    private String fileName;
    private String status;
    private Instant uploadTime;
    private Instant parsedTime;
    private String parsedText; // consider truncation

    public static ResumeResponseDto from(Resume r) {
        ResumeResponseDto d = new ResumeResponseDto();
        d.setId(r.getId());
        d.setUserId(r.getUserId());
        d.setFileName(r.getFileName());
        d.setStatus(r.getStatus().name());
        d.setUploadTime(r.getUploadedAt());
        d.setParsedTime(r.getParsedTime());
        String parsed = r.getParsedText();
        d.setParsedText(parsed == null ? null : (parsed.length() > 20_000 ? parsed.substring(0, 20_000) : parsed));
        return d;
    }
}
