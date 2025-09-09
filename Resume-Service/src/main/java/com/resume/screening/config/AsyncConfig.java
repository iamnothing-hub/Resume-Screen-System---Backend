package com.resume.screening.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;



/*
* This is for asynchronous processing
* when we have to process multiple resumes at a time
* we can use this configuration to create a thread pool
* and use it in our service class
* by using @Async annotation
* and specify the executor name
* @Async("parserExecutor")
* */


@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "parserExecutor")
    public Executor parserExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("parser-");
        executor.initialize();

        return executor;
    }
}
