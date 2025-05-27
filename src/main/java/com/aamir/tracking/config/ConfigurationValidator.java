package com.aamir.tracking.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigurationValidator {

    @Value("${tracking.id.pattern}")
    private String pattern;

    @Value("${tracking.id.length}")
    private int idLength;

    @Value("${tracking.id.instance.id}")
    private String instanceId;

    @Value("${tracking.id.start.range}")
    private int startRange;

    @Value("${tracking.id.end.range}")
    private int endRange;

    @Value("${tracking.id.padding}")
    private int padding;

    @PostConstruct
    public void validateConfiguration() {
        log.info("Validating tracking number service configuration");

        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalStateException("Tracking ID pattern must be defined");
        }

        if (idLength <= 0 || idLength > 16) {
            throw new IllegalStateException("Tracking ID length must be between 1 and 16");
        }

        if (instanceId.length() > 99) {
            throw new IllegalStateException("Instance ID must be between 0 and 99");
        }

        if (startRange >= endRange) {
            throw new IllegalStateException("Start range must be less than end range");
        }

        if (padding <= 0) {
            throw new IllegalStateException("Padding must be greater than 0");
        }

        log.info("Tracking number service configuration validated successfully");
    }


}
