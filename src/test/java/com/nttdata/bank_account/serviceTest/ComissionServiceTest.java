package com.nttdata.bank_account.serviceTest;

import com.nttdata.bank_account.model.entity.Commission;
import com.nttdata.bank_account.repository.CommissionRepository;
import com.nttdata.bank_account.service.impl.CommissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComissionServiceTest {
    @Mock
    private CommissionRepository commissionRepository;

    @InjectMocks
    private CommissionServiceImpl commissionService;

    private String accountId;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    public void setUp() {
        accountId = "testAccountId";
        startDate = new Date(2023, 1, 1);
        endDate = new Date(2023, 12, 31);
    }

    @Test
    public void testGetCommissionsByAccountIdAndDateRangeSuccess() {
        Commission commission = new Commission();
        when(commissionRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate))
                .thenReturn(Flux.just(commission));

        StepVerifier.create(commissionService.getCommissionsByAccountIdAndDateRange(accountId, startDate, endDate))
                .expectNext(commission)
                .verifyComplete();
    }

    @Test
    public void testGetCommissionsByAccountIdAndDateRangeNoCommissionsFound() {
        when(commissionRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate))
                .thenReturn(Flux.empty());

        StepVerifier.create(commissionService.getCommissionsByAccountIdAndDateRange(accountId, startDate, endDate))
                .expectError(Exception.class)
                .verify();
    }

    @Test
    public void testGetCommissionsByAccountIdAndDateRangeError() {
        when(commissionRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(commissionService.getCommissionsByAccountIdAndDateRange(accountId, startDate, endDate))
                .expectError(Exception.class)
                .verify();
    }
}
