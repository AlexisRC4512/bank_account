package com.nttdata.bank_account.repository;

import com.nttdata.bank_account.model.entity.Commission;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Date;


@Repository
public interface CommissionRepository extends ReactiveMongoRepository<Commission, String> {
    Flux<Commission> findByAccountIdAndDateBetween(String accountId, Date startDate, Date endDate);
}
