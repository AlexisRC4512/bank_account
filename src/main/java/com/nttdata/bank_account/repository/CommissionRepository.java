package com.nttdata.bank_account.repository;

import com.nttdata.bank_account.model.entity.Commission;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommissionRepository extends ReactiveMongoRepository<Commission, String> {

}
