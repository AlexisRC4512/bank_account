package com.nttdata.bank_account.repository;

import com.nttdata.bank_account.model.entity.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account,String> {
    Flux<Account>findByClientId(String id);
    Mono<Account>findByNumberAccount(int id);
}
