package com.ninjaone.dundie_awards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class AppPropertiesTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private AppProperties appProperties;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private AppProperties.RetrySaveActivityConfig retrySaveActivityConfig;

    @BeforeEach
    void setup() {
        given(appProperties.batchSize()).willReturn(1000);
        given(appProperties.messageProcessingIntervalMs()).willReturn(7000);
        given(appProperties.retrySaveActivity()).willReturn(retrySaveActivityConfig);

        given(retrySaveActivityConfig.maxAttempts()).willReturn(3);
        given(retrySaveActivityConfig.backoffDelayMs()).willReturn(3000);
    }

    @Test
    void shouldReturnConfiguredBatchSize() {
        int batchSize = appProperties.batchSize();

        assertThat(batchSize).isEqualTo(1000);
    }

    @Test
    void shouldReturnConfiguredMessageProcessingIntervalMs() {
        int intervalMs = appProperties.messageProcessingIntervalMs();

        assertThat(intervalMs).isEqualTo(7000);
    }

    @Test
    void shouldReturnRetrySaveActivityMaxAttempts() {
        given(appProperties.retrySaveActivity()).willReturn(retrySaveActivityConfig);
        given(retrySaveActivityConfig.maxAttempts()).willReturn(3);

        int maxAttempts = appProperties.retrySaveActivity().maxAttempts();

        assertThat(maxAttempts).isEqualTo(3);
    }

    @Test
    void shouldReturnRetrySaveActivityBackoffDelayMs() {
        given(appProperties.retrySaveActivity()).willReturn(retrySaveActivityConfig);
        given(retrySaveActivityConfig.backoffDelayMs()).willReturn(3000);

        int backoffDelayMs = appProperties.retrySaveActivity().backoffDelayMs();

        assertThat(backoffDelayMs).isEqualTo(3000);
    }
}
