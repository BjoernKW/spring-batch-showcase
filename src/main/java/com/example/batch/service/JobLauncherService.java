package com.example.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/api/jobs")
public class JobLauncherService {
    
    private static final Logger logger = LoggerFactory.getLogger(JobLauncherService.class);
    
    private final JobLauncher jobLauncher;
    private final Job csvProcessingJob;
    private final Job databaseProcessingJob;

    public JobLauncherService(JobLauncher jobLauncher,
                             @Qualifier("csvProcessingJob") Job csvProcessingJob,
                             @Qualifier("databaseProcessingJob") Job databaseProcessingJob) {
        this.jobLauncher = jobLauncher;
        this.csvProcessingJob = csvProcessingJob;
        this.databaseProcessingJob = databaseProcessingJob;
    }

    @GetMapping("/csv")
    public String launchCsvJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            
            JobExecution execution = jobLauncher.run(csvProcessingJob, jobParameters);
            logger.info("CSV Job launched with execution id: {}", execution.getId());
            return "CSV Job started successfully. Execution ID: " + execution.getId();
        } catch (Exception e) {
            logger.error("Failed to launch CSV job", e);
            return "Failed to start CSV job: " + e.getMessage();
        }
    }

    @GetMapping("/database")
    public String launchDatabaseJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            
            JobExecution execution = jobLauncher.run(databaseProcessingJob, jobParameters);
            logger.info("Database Job launched with execution id: {}", execution.getId());
            return "Database Job started successfully. Execution ID: " + execution.getId();
        } catch (Exception e) {
            logger.error("Failed to launch Database job", e);
            return "Failed to start Database job: " + e.getMessage();
        }
    }

    @GetMapping("/all")
    public String launchAllJobs() {
        StringBuilder result = new StringBuilder();
        
        try {
            // Launch CSV job
            JobParameters csvJobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("jobType", "csv")
                    .toJobParameters();
            
            JobExecution csvExecution = jobLauncher.run(csvProcessingJob, csvJobParameters);
            result.append("CSV Job started. Execution ID: ").append(csvExecution.getId()).append("\n");
            
            // Launch Database job
            JobParameters dbJobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("jobType", "database")
                    .toJobParameters();
            
            JobExecution dbExecution = jobLauncher.run(databaseProcessingJob, dbJobParameters);
            result.append("Database Job started. Execution ID: ").append(dbExecution.getId());
            
            return result.toString();
        } catch (Exception e) {
            logger.error("Failed to launch jobs", e);
            return "Failed to start jobs: " + e.getMessage();
        }
    }
}
