package com.resume.screening.service.impl;

import com.resume.screening.dto.JobDTO;
import com.resume.screening.entity.JobEntity;
import com.resume.screening.repository.JobRepository;
import com.resume.screening.service.JobService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper mapper;

  /*  public JobServiceImpl(JobRepository jobRepository, ModelMapper mapper) {
        this.jobRepository = jobRepository;
        this.mapper = mapper;
    }*/

    @Override
    public JobDTO createJob(JobDTO job) {
        job.setJobPostedAt(LocalDateTime.now());
        System.out.println(job.toString());
        JobEntity saved = (JobEntity)jobRepository.save(mapper.map(job, JobEntity.class));

        return mapper.map(saved, JobDTO.class);
    }

    @Override
    public JobDTO getJob(Long jobId) {
        JobEntity jobEntity = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job is not exist" + jobId));

        return mapper.map(jobEntity, JobDTO.class);
    }

    @Override
    public ArrayList<JobDTO> getAllJobs() {

        List<JobEntity> jobs = jobRepository.findAll();

        ArrayList<JobDTO> dtos = new ArrayList<>();
        for( JobEntity job : jobs){
            dtos.add(mapper.map(job, JobDTO.class));

        }
        return dtos;
    }
}
