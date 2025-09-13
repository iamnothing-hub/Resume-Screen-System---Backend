package com.resume.screening.service;

import com.resume.screening.dto.JobDTO;

import java.util.ArrayList;

public interface JobService {

    JobDTO createJob(JobDTO job);
    JobDTO getJob(Long jobId);
    ArrayList<JobDTO> getAllJobs();
}
