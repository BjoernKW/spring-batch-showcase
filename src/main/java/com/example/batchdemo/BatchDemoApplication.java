package com.example.batchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class BatchDemoApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job csvToCsvJob;

    @Autowired
    private Job mySqlToPostgresJob;

    public static void main(String[] args) {
        SpringApplication.run(BatchDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        jobLauncher.run(csvToCsvJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
        jobLauncher.run(mySqlToPostgresJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
    }
}

@RestController
@RequestMapping("/batch")
class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job csvToCsvJob;

    @Autowired
    private Job mySqlToPostgresJob;

    @PostMapping("/csv-to-csv")
    public String launchCsvToCsvJob() throws Exception {
        jobLauncher.run(csvToCsvJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
        return "CSV to CSV Job started";
    }

    @PostMapping("/mysql-to-postgres")
    public String launchMySqlToPostgresJob() throws Exception {
        jobLauncher.run(mySqlToPostgresJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
        return "MySQL to PostgreSQL Job started";
    }
}
