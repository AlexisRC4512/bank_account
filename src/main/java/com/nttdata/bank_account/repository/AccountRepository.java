package com.nttdata.bank_account.repository;

import com.nttdata.bank_account.model.entity.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account,String> {
}
