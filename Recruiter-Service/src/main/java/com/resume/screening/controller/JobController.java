package com.resume.screening.controller;


import com.resume.screening.dto.JobDTO;
import com.resume.screening.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping("/createJob")
    public ResponseEntity<JobDTO> createJob(@RequestBody JobDTO jobDTO){
        JobDTO job = jobService.createJob(jobDTO);
        return new ResponseEntity<>(job, HttpStatus.CREATED);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDTO> getJob(@PathVariable Long jobId){
        return new ResponseEntity<>(jobService.getJob(jobId), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<ArrayList<JobDTO>> getAllJobs(){
        return new ResponseEntity<>(jobService.getAllJobs(), HttpStatus.OK);
    }



}
