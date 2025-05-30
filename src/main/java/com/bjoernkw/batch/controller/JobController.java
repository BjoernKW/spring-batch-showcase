package com.bjoernkw.batch.controller;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class JobController {

  private final JobExplorer jobExplorer;
  private final JobOperator jobOperator;

  public JobController(JobExplorer jobExplorer, JobOperator jobOperator) {
    this.jobExplorer = jobExplorer;
    this.jobOperator = jobOperator;
  }

  @GetMapping("/status")
  public Map<String, Object> getJobStatus() {
    Map<String, Object> status = new HashMap<>();

    try {
      // Get job names
      status.put("availableJobs", jobExplorer.getJobNames());

      // Get running executions
      status.put("runningExecutions", jobOperator.getRunningExecutions("csvProcessingJob"));
      status.put("runningDatabaseExecutions", jobOperator.getRunningExecutions("databaseProcessingJob"));

    } catch (Exception e) {
      status.put("error", e.getMessage());
    }

    return status;
  }

  @GetMapping("/health")
  public Map<String, String> health() {
    Map<String, String> health = new HashMap<>();
    health.put("status", "UP");
    health.put("application", "Spring Batch Showcase");
    return health;
  }
}
