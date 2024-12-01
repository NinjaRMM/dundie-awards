package com.ninjaone.dundie_awards;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DundieAwardsApplicationTests {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Test
    void contextLoads() {
        assertThat(taskScheduler).isNotNull();
    }

    @Test
    void shouldConfigureTaskScheduler() {
        String threadNamePrefix = taskScheduler.getThreadNamePrefix();
        assertThat(threadNamePrefix).isEqualTo("messageBroker-");
    }
}
