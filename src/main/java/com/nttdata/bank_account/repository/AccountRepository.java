package com.nttdata.bank_account.repository;

import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.response.AccountResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account,String> {
    Flux<Account>findByClientId(String id);
}
