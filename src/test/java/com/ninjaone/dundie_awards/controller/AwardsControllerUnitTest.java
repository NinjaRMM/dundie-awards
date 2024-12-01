package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.controller.rest.AwardsControllerImpl;
import com.ninjaone.dundie_awards.service.AwardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class AwardsControllerUnitTest {

    @Mock
    private AwardsCache awardsCache;

    @Mock
    private MessageBroker messageBroker;

    @Mock
    private AwardService awardService;

    @InjectMocks
    private AwardsControllerImpl awardsController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class GiveOrganizationDundieAwardsTests {

        @Test
        void shouldGiveDundieAwardsToOrganization() {
            long organizationId = 1L;

            awardsController.giveOrganizationDundieAwards(organizationId);

            ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
            ArgumentCaptor<Long> organizationIdCaptor = ArgumentCaptor.forClass(Long.class);

            verify(awardService).giveDundieAwards(uuidCaptor.capture(), organizationIdCaptor.capture());

            assertThat(uuidCaptor.getValue()).isNotNull();
            assertThat(organizationIdCaptor.getValue()).isEqualTo(organizationId);
        }
    }
}
