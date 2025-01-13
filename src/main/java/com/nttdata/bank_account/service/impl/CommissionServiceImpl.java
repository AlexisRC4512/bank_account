package com.nttdata.bank_account.service.impl;

import com.nttdata.bank_account.model.entity.Commission;
import com.nttdata.bank_account.model.exception.AccountNotFoundException;
import com.nttdata.bank_account.repository.CommissionRepository;
import com.nttdata.bank_account.service.CommissionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Log4j2
@Service
public class CommissionServiceImpl implements CommissionService {
    @Autowired
    private CommissionRepository commissionRepository;

    @Override
    public Flux<Commission> getCommissionsByAccountIdAndDateRange(String accountId, Date startDate, Date endDate) {
        return commissionRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("No commissions found for account ID: " + accountId)))
                .doOnError(e -> log.error("Error fetching commissions for account ID: {}", accountId, e))
                .onErrorMap(e -> new Exception("Error fetching commissions for account ID", e));
    }
}

