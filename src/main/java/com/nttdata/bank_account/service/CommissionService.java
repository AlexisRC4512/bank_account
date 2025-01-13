package com.nttdata.bank_account.service;

import com.nttdata.bank_account.model.entity.Commission;
import reactor.core.publisher.Flux;

import java.util.Date;

public interface CommissionService {
    public Flux<Commission>getCommissionsByAccountIdAndDateRange(String accountId, Date startDate, Date endDate);
}
