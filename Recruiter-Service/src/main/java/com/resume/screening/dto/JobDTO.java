package com.resume.screening.dto;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class JobDTO {

    private Long jobId;
    private String companyName;
    private String jobRole;
    private String location;
    private String experience;
    private String salary;
    private String skills;
    private String responsibility;
    private LocalDateTime jobPostedAt;
}
