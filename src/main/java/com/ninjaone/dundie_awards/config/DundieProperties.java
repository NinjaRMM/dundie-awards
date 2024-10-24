package com.ninjaone.dundie_awards.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("dundie")
public record DundieProperties(
        @Min(1)
        @NotNull
        int messageBrokerSize,

        @Min(1)
        @NotNull
        int messageBrokerTimeoutMs
) {
}
