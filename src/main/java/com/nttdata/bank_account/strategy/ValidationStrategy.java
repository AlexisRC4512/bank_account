package com.nttdata.bank_account.strategy;

import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.enums.AccountType;
import com.nttdata.bank_account.model.enums.TypeClient;
import com.nttdata.bank_account.model.exception.AccountException;
import com.nttdata.bank_account.model.response.AccountResponse;
import com.nttdata.bank_account.repository.AccountRepository;
import com.nttdata.bank_account.util.AccountConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ValidationStrategy {

    private final AccountRepository accountRepository;

    public ValidationStrategy(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public final Map<String, Function<Account, Mono<AccountResponse>>> validationStrategies = Map.of(
            TypeClient.PERSONAL.name(), this::validatePersonalClient,
            TypeClient.BUSINESS.name(), this::validateBusinessClient
    );
    private Mono<AccountResponse> validatePersonalClient(Account accountObj) {
        return accountRepository.findByClientId(accountObj.getClientId())
                .collectList()
                .flatMap(existingAccounts -> {
                    if (hasExceededPersonalAccountLimits(existingAccounts, accountObj)) {
                        return Mono.error(new AccountException("Personal client can only have one of each account type"));
                    }
                    return saveAccount(accountObj);
                });
    }

    private boolean hasExceededPersonalAccountLimits(List<Account> existingAccounts, Account accountObj) {
        long savingsCount = existingAccounts.stream()
                .filter(acc -> acc.getType().equals(AccountType.SAVINGS))
                .count();
        long currentCount = existingAccounts.stream()
                .filter(acc -> acc.getType().equals(AccountType.CURRENT))
                .count();
        long fixedTermCount = existingAccounts.stream()
                .filter(acc -> acc.getType().equals(AccountType.FIXED_TERM))
                .count();

        return (accountObj.getType().equals(AccountType.SAVINGS) && savingsCount >= 1) ||
                (accountObj.getType().equals(AccountType.CURRENT) && currentCount >= 1) ||
                (accountObj.getType().equals(AccountType.FIXED_TERM) && fixedTermCount >= 1);
    }

    private Mono<AccountResponse> validateBusinessClient(Account accountObj) {
        if (accountObj.getType().equals(AccountType.SAVINGS) || accountObj.getType().equals(AccountType.FIXED_TERM)) {
            return Mono.error(new AccountException("Business client cannot have savings or fixed term accounts"));
        }
        return saveAccount(accountObj);
    }

    private Mono<AccountResponse> saveAccount(Account accountObj) {
        return accountRepository.save(accountObj)
                .map(AccountConverter::toAccountResponse);
    }
}
