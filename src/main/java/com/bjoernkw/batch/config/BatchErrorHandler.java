package com.bjoernkw.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class BatchErrorHandler implements SkipListener<Object, Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchErrorHandler.class);

    @Override
    public void onSkipInRead(Throwable t) {
        logger.warn("Skipped item in read phase due to: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        logger.warn("Skipped item in write phase. Item: {}, Error: {}", item, t.getMessage());
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        logger.warn("Skipped item in process phase. Item: {}, Error: {}", item, t.getMessage());
    }
}
