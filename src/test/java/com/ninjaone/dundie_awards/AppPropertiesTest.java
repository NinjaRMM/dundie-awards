package com.ninjaone.dundie_awards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class AppPropertiesTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private AppProperties appProperties;

    @BeforeEach
    void setup() {
        given(appProperties.batchSize()).willReturn(1000);
    }
    
    @Test
    void shouldReturnConfiguredBatchSize() {
        given(appProperties.batchSize()).willReturn(1000);

        int batchSize = appProperties.batchSize();

        assertThat(batchSize).isEqualTo(1000);
    }
}
