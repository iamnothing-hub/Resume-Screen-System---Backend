package com.resume.screening.clientservice;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "RECRUITER_SERVICE")
public interface RecruiterClient {
}
