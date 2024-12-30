package com.nttdata.bank_account.interfaces;

import com.nttdata.bank_account.model.entity.Account;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface TransactionLimitChecker {
    Mono<Void> check(Account account);
}
