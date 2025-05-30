package com.bjoernkw.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job '{}' started at {}", 
                   jobExecution.getJobInstance().getJobName(), 
                   jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info(
                "Job '{}' completed successfully at {}.",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getEndTime()
            );
            
            logger.info(
                "Job Statistics - Read: {}, Written: {}, Skipped: {}",
                jobExecution.getStepExecutions().iterator().next().getReadCount(),
                jobExecution.getStepExecutions().iterator().next().getWriteCount(),
                jobExecution.getStepExecutions().iterator().next().getSkipCount()
            );
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            logger.error(
                "Job '{}' failed at {}. Failures: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getEndTime(),
                jobExecution.getAllFailureExceptions()
            );
        }
    }
}
