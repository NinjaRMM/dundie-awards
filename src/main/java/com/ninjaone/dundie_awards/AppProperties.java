package com.ninjaone.dundie_awards;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app")
public record AppProperties(
        @Min(1) @NotNull int batchSize,
        @Min(7000) @NotNull int messageProcessingIntervalMs,
        @NotNull RetrySaveActivity retrySaveActivity,
        @NotNull RetryRollback retryRollback,
        @NotNull boolean enableTestBehavior
) {
    public record RetrySaveActivity(
            @Min(0) @NotNull int maxAttempts,
            @Min(0) @NotNull int backoffDelayMs
    ) {}
    public record RetryRollback(
    		@Min(0) @NotNull int maxAttempts,
    		@Min(0) @NotNull int backoffDelayMs
    		) {}
}
