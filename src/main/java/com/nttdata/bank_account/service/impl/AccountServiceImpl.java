package com.nttdata.bank_account.service.impl;

import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.entity.Client;
import com.nttdata.bank_account.model.enums.AccountType;
import com.nttdata.bank_account.model.enums.TypeClient;
import com.nttdata.bank_account.model.exception.AccountException;
import com.nttdata.bank_account.model.exception.AccountNotFoundException;
import com.nttdata.bank_account.model.request.AccountRequest;
import com.nttdata.bank_account.model.response.AccountResponse;
import com.nttdata.bank_account.repository.AccountRepository;
import com.nttdata.bank_account.service.AccountService;
import com.nttdata.bank_account.service.ClientService;
import com.nttdata.bank_account.strategy.ValidationStrategy;
import com.nttdata.bank_account.util.AccountConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;


/**
 * Implementation of the account service.
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ValidationStrategy validationStrategy;

    /**
     * Retrieves all accounts.
     *
     * @return a flux of account responses.
     */
    @Override
    public Flux<AccountResponse> getAllAccounts() {
        log.info("Fetching all Accounts");
        return accountRepository.findAll()
                .map(AccountConverter::toAccountResponse)
                .onErrorMap(e->new Exception("Error fetching all accounts",e));
    }

    /**
     * Creates a new account.
     *
     * @param account the account request.
     * @return an account response.
     */
    @Override
    public Mono<AccountResponse> createAccount(AccountRequest account) {
        if (account == null) {
            log.warn("Invalid client account: {}", account);
            return Mono.error(new AccountException("Invalid client data"));
        }
        log.info("Creating new account: {}", account.getType());
        Account accountObj = AccountConverter.toAccount(account);
        Mono<Client> clientMono = clientService.getClientById(accountObj.getClientId());

        return clientMono.flatMap(client -> validateAndSaveAccount(client, accountObj))
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with Client id: ")))
                .doOnError(e -> log.error("Error creating account", e))
                .onErrorMap(e -> new Exception("Error creating account", e));
    }

    private Mono<AccountResponse> validateAndSaveAccount(Client client, Account accountObj) {
        String clientType = client.getType();
        Function<Account, Mono<AccountResponse>> validationFunction = validationStrategy.validationStrategies.get(clientType);

        if (validationFunction != null) {
            return validationFunction.apply(accountObj);
        } else {
            return Mono.error(new AccountException("Unknown client type"));
        }
    }
    /**
     * Retrieves an account by its ID.
     *
     * @param id the account ID.
     * @return an account response.
     */
    @Override
    public Mono<AccountResponse> getAccountById(String id) {
        log.info("Fetching account with id: {}", id);
        return accountRepository.findById(id)
                .map(AccountConverter::toAccountResponse)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: " + id)))
                .doOnError(e -> log.error("Error fetching account with id: {}", id, e))
                .onErrorMap(e -> new Exception("Error fetching account by id", e));
    }

    /**
     * Updates an existing account.
     *
     * @param id      the account ID.
     * @param account the account request.
     * @return an account response.
     */
    @Override
    public Mono<AccountResponse> updateAccount(String id, AccountRequest account) {
        log.info("Updating account with id: {}", id);
        return accountRepository.findById(id)
                .flatMap(existingAccount -> {
                    Account updatedAccount = AccountConverter.toAccount(account);
                    updatedAccount.setId(existingAccount.getId());
                    return accountRepository.save(updatedAccount);
                })
                .map(AccountConverter::toAccountResponse)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: \" + id")))
                .doOnError(e -> log.error("Error updating account with id: {}", id, e))
                .onErrorMap(e -> new Exception("Error updating account", e));
    }

    /**
     * Deletes an account by its ID.
     *
     * @param id the account ID.
     * @return a void Mono.
     */
    @Override
    public Mono<Void> deleteAccount(String id) {
        log.info("Deleting account with id: {}", id);
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: \" + id")))
                .flatMap(existingAccount -> accountRepository.delete(existingAccount))
                .doOnError(e -> log.error("Error deleting account with id: {}", id, e))
                .onErrorMap(e -> new Exception("Error deleting account", e));
    }




}
