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

/**
 * Strategy class for validating accounts based on client type.
 */
@Component
public class ValidationStrategy {

    private final AccountRepository accountRepository;

    /**
     * Constructs a ValidationStrategy with the specified AccountRepository.
     *
     * @param accountRepository the repository for accessing account data
     */
    public ValidationStrategy(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Map of validation strategies based on client type.
     */
    public final Map<String, Function<Account, Mono<AccountResponse>>> validationStrategies = Map.of(
            TypeClient.PERSONAL.name(), this::validatePersonalClient,
            TypeClient.BUSINESS.name(), this::validateBusinessClient
    );

    /**
     * Validates a personal client's account.
     *
     * @param accountObj the account to validate
     * @return a Mono emitting the AccountResponse if validation passes, or an error if validation fails
     */
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

    /**
     * Checks if a personal client has exceeded account limits.
     *
     * @param existingAccounts the list of existing accounts
     * @param accountObj the account to validate
     * @return true if the limits are exceeded, false otherwise
     */
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

    /**
     * Validates a business client's account.
     *
     * @param accountObj the account to validate
     * @return a Mono emitting the AccountResponse if validation passes, or an error if validation fails
     */
    private Mono<AccountResponse> validateBusinessClient(Account accountObj) {
        if (accountObj.getType().equals(AccountType.SAVINGS) || accountObj.getType().equals(AccountType.FIXED_TERM)) {
            return Mono.error(new AccountException("Business client cannot have savings or fixed term accounts"));
        }
        return saveAccount(accountObj);
    }

    /**
     * Saves the account and converts it to an AccountResponse.
     *
     * @param accountObj the account to save
     * @return a Mono emitting the AccountResponse
     */
    private Mono<AccountResponse> saveAccount(Account accountObj) {
        return accountRepository.save(accountObj)
                .map(AccountConverter::toAccountResponse);
    }
}
