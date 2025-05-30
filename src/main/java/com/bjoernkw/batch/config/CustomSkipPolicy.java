package com.bjoernkw.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipPolicy implements SkipPolicy {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomSkipPolicy.class);
    
    private static final int MAX_SKIP_COUNT = 5;

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        if (skipCount >= MAX_SKIP_COUNT) {
            logger.error("Skip limit exceeded. Total skips: {}", skipCount);
            return false;
        }

        if (throwable instanceof IllegalArgumentException ||
            throwable instanceof NumberFormatException ||
            throwable instanceof NullPointerException) {
            logger.warn("Skipping item due to: {} (Skip count: {})", throwable.getMessage(), skipCount + 1);
            return true;
        }

        logger.error("Non-skippable exception occurred", throwable);
        return false;
    }
}
