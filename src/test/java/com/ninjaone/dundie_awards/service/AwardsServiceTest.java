package com.ninjaone.dundie_awards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ninjaone.dundie_awards.AppProperties;
import com.ninjaone.dundie_awards.AwardsCache;
import com.ninjaone.dundie_awards.MessageBroker;
import com.ninjaone.dundie_awards.event.Event;
import com.ninjaone.dundie_awards.event.EventType;
import com.ninjaone.dundie_awards.service.impl.AwardServiceImpl;

class AwardsServiceTest {

    @Mock
    private AppProperties appProperties;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AwardOperationLogService awardOperationLogService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private AwardsCache awardsCache;

    @Mock
    private MessageBroker messageBroker;

    @InjectMocks
    private AwardServiceImpl awardsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessAwardsSuccessfully() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        long organizationId = 1L;
        String rollbackData = "1,5|2,3|3,2"; 
        int updatedRecords = 10; 

        given(employeeService.fetchEmployeeRollbackData(uuid, organizationId)).willReturn(rollbackData);
        given(employeeService.addDundieAwardToEmployees(eq(uuid), eq(organizationId))).willReturn(updatedRecords);

        int totalUpdatedRecords = awardsService.giveDundieAwards(uuid, organizationId);

        assertThat(totalUpdatedRecords).isEqualTo(updatedRecords);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(messageBroker).sendMessage(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();

        assertThat(capturedEvent.uuid().toString()).isEqualTo(uuid.toString());
        assertThat(capturedEvent.eventType()).isEqualTo(EventType.AWARD_ORGANIZATION_SUCCESS_EVENT);
        assertThat(capturedEvent.totalAffectedEmployees()).isEqualTo(updatedRecords);
        assertThat(capturedEvent.totalAwards()).isEqualTo(updatedRecords);

        verify(employeeService).fetchEmployeeRollbackData(uuid, organizationId);
        verify(awardOperationLogService).createAwardOperationLog(eq(uuid), any(Instant.class), eq(rollbackData));
        verify(employeeService).addDundieAwardToEmployees(eq(uuid), eq(organizationId));
        verify(organizationService).block(uuid, organizationId);
        verify(awardsCache).addAwards(updatedRecords);
    }

}
