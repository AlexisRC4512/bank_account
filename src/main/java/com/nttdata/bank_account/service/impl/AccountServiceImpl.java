package com.nttdata.bank_account.service.impl;

import com.nttdata.bank_account.interfaces.TransactionLimitChecker;
import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.entity.Client;
import com.nttdata.bank_account.model.entity.Transaction;
import com.nttdata.bank_account.model.entity.Commission;
import com.nttdata.bank_account.model.enums.SubTypeClient;
import com.nttdata.bank_account.model.enums.TypeTransaction;
import com.nttdata.bank_account.model.exception.AccountException;
import com.nttdata.bank_account.model.exception.AccountNotFoundException;
import com.nttdata.bank_account.model.exception.InsufficientFundsException;
import com.nttdata.bank_account.model.request.AccountRequest;
import com.nttdata.bank_account.model.request.TransactionRequest;
import com.nttdata.bank_account.model.request.TransferRequest;
import com.nttdata.bank_account.model.response.*;
import com.nttdata.bank_account.repository.AccountRepository;
import com.nttdata.bank_account.repository.CommissionRepository;
import com.nttdata.bank_account.service.AccountService;
import com.nttdata.bank_account.service.ClientService;
import com.nttdata.bank_account.service.CreditCardService;
import com.nttdata.bank_account.strategy.ValidationStrategy;
import com.nttdata.bank_account.util.AccountConverter;
import com.nttdata.bank_account.util.BalanceConverter;
import com.nttdata.bank_account.util.ComissionConverter;
import com.nttdata.bank_account.util.TransactionConverter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Implementation of the account service.
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private  AccountRepository accountRepository;
    private  ClientService clientService;
    private  ValidationStrategy validationStrategy;
    private CommissionRepository commissionRepository;
    private  CreditCardService creditCardService;

    public AccountServiceImpl(AccountRepository accountRepository, ClientService clientService, ValidationStrategy validationStrategy, CommissionRepository commissionRepository, CreditCardService creditCardService) {
        this.accountRepository = accountRepository;
        this.clientService = clientService;
        this.validationStrategy = validationStrategy;
        this.commissionRepository = commissionRepository;
        this.creditCardService = creditCardService;
    }

    /**
     * Retrieves all accounts.
     *
     * @return a flux of account responses.
     */
    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackGetAllAccounts")
    @TimeLimiter(name = "bank-account")
    public Flux<AccountResponse> getAllAccounts() {
        log.info("Fetching all Accounts");
        return accountRepository.findAll()
                .map(AccountConverter::toAccountResponse)
                .onErrorMap(e -> new Exception("Error fetching all accounts",e));
    }

    /**
     * Creates a new account.
     *
     * @param account the account request.
     * @return an account response.
     */
    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackCreateAccount")
    @TimeLimiter(name = "bank-account")
    public Mono<AccountResponse> createAccount(AccountRequest account) {
        if (account == null) {
            log.warn("Invalid client account: {}", account);
            return Mono.error(new AccountException("Invalid client data"));
        }
        Mono<Account> accountMonoNumberAccountValidator = accountRepository.findByNumberAccount(account.getNumberAccount());
        return accountMonoNumberAccountValidator
                .hasElement()
                .flatMap(hasAccount -> {
                    if (hasAccount) {
                        return Mono.error(new AccountException("That account number already exists"));
                    } else {
                        log.info("Creating new account: {}", account.getType());
                        SubTypeClient[] listSubType = SubTypeClient.values();
                        List<String> subTypeNames = Arrays.stream(listSubType)
                                .map(Enum::name)
                                .collect(Collectors.toList());
                        Account accountObj = AccountConverter.toAccount(account);
                        Mono<Client> clientMono = clientService.getClientById(accountObj.getClientId());
                        return clientMono.flatMap(client -> validateAndSaveAccount(client, accountObj, subTypeNames))
                                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with Client id: ")))
                                .doOnError(e -> log.error("Error creating account", e))
                                .onErrorMap(e -> new Exception("Error creating account", e));
                    }
                });
    }

    private Mono<AccountResponse> validateAndSaveAccount(Client client, Account accountObj ,List<String>listSubType) {
        String clientType = client.getType();
        Function<Account, Mono<AccountResponse>> validationFunction = validationStrategy.validationStrategies.get(clientType);

        if (validationFunction != null) {
            if (listSubType.contains(client.getSubType())) {
                return creditCardService.getClientById(accountObj.getClientId())
                        .collectList()
                        .flatMap(creditCardList -> {
                            return Flux.fromIterable(creditCardList)
                                    .switchIfEmpty(Mono.error(new AccountException("Client must have a credit card")))
                                    .then(validationFunction.apply(accountObj));
                        });

            }
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
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackGetAccountById")
    @TimeLimiter(name = "bank-account")
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
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackUpdateAccount")
    @TimeLimiter(name = "bank-account")
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
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackDeleteAccount")
    @TimeLimiter(name = "bank-account")
    public Mono<Void> deleteAccount(String id) {
        log.info("Deleting account with id: {}", id);
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: \" + id")))
                .flatMap(existingAccount -> accountRepository.delete(existingAccount))
                .doOnError(e -> log.error("Error deleting account with id: {}", id, e))
                .onErrorMap(e -> new Exception("Error deleting account", e));
    }
    /**
     * Withdraws an amount from the specified account.
     *
     * @param idAccount the ID of the account
     * @param transactionRequest the transaction request containing the amount to withdraw
     * @return a Mono emitting the TransactionResponse if the withdrawal is successful, or an error if it fails
     */
    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackWithdraw")
    @TimeLimiter(name = "bank-account")
    public Mono<TransactionResponse> withdraw(String idAccount, TransactionRequest transactionRequest) {
        log.info("Witdraw mount about account");
        if (idAccount.isEmpty()) {
            log.warn("Invalid client account: {}", idAccount);
            return Mono.error(new AccountException("Invalid client data"));
        }
        return accountRepository.findById(idAccount)
                .flatMap(account -> {
                    if (account.getBalance() < transactionRequest.getAmount()) {
                        log.warn("Insufficient balance for account: {}", idAccount);
                        return Mono.error(new AccountException("Insufficient funds"));
                    }
                    if (String.valueOf(account.getTransactionCount()) == null) {
                        account.setTransactionCount(0);
                    }
                    account.incrementTransactionCount();
                    account.setBalance(Math.round(account.getBalance() - transactionRequest.getAmount()));
                    Mono<Transaction> transactionUpdate = TransactionConverter.toTransaction(transactionRequest, account.getClientId(), TypeTransaction.WITHDRAWAL, "new Transaction");
                    return updateTransaction( transactionUpdate,account);
                }).switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: \" + id")))
                .doOnError(e -> log.error("Error withdrawing ", e))
                .onErrorMap(e -> new Exception("Error withdrawing account", e));

    }
    /**
     * Deposits an amount into the specified account.
     *
     * @param idAccount the ID of the account
     * @param transactionRequest the transaction request containing the amount to deposit
     * @return a Mono emitting the TransactionResponse if the deposit is successful, or an error if it fails
     */
    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackDeposit")
    @TimeLimiter(name = "bank-account")
    public Mono<TransactionResponse> deposit(String idAccount, TransactionRequest transactionRequest) {
        log.info("deposit mount about account");
        if (idAccount.isEmpty()) {
            log.warn("Invalid client account: {}", idAccount);
            return Mono.error(new AccountException("Invalid client data"));
        }
        return accountRepository.findById(idAccount)
                .flatMap(account -> {
                    if (String.valueOf(account.getTransactionCount()) == null) {
                        account.setTransactionCount(0);
                    }
                    account.incrementTransactionCount();
                    account.setBalance(Math.round(account.getBalance() + transactionRequest.getAmount()));
                    Mono<Transaction> transactionUpdate = TransactionConverter.toTransaction(transactionRequest, account.getClientId(), TypeTransaction.DEPOSIT, "new Transaction");
                    return updateTransaction( transactionUpdate,account);
                }).switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: \" + id")))
                .doOnError(e -> log.error("Error deposit ", e))
                .onErrorMap(e -> new Exception("Error deposit account", e));
    }
    /**
     * Retrieves the balance for all accounts associated with the specified client ID.
     *
     * @param idClient the ID of the client
     * @return a Flux emitting BalanceResponse objects for each account, or an error if no accounts are found
     */
    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackGetBalanceByClientId")
    @TimeLimiter(name = "bank-account")
    public Flux<BalanceResponse> getBalanceByClientId(String idClient) {
        return accountRepository.findByClientId(idClient)
                .map(account -> {
                    if (account == null) {
                        throw new AccountNotFoundException("Account not found with id: " + idClient);
                    }
                    return BalanceConverter.toBalanceResponse(Collections.singletonList(account));
                })
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Account not found with id: " + idClient)))
                .doOnError(e -> log.error("Error getting balance for account", e))
                .onErrorMap(e -> new Exception("Error getting balance for account", e));
    }
    /**
     * Retrieves the transaction history for the specified account ID.
     *
     * @param id the ID of the account
     * @return a Mono emitting the TransactionAccountResponse if the account is found, or an error if it is not
     */
    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackGetTransactionByAccount")
    @TimeLimiter(name = "bank-account")
    public Mono<TransactionAccountResponse> getTransactionByAccount(String id) {
        return accountRepository.findById(id).map(TransactionConverter::toTransactionAccountResponse)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with id: " + id)))
                .doOnError(e -> log.error("Error fetching account with id: {}", id, e))
                .onErrorMap(e -> new Exception("Error fetching account by id", e));
    }

    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackTransferInternal")
    @TimeLimiter(name = "bank-account")
    public Mono<TransferResponse> transferInternal(TransferRequest request) {
        return accountRepository.findByNumberAccount(request.getFromAccountNumber())
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with getFromAccountNumber ")))
                .zipWith(accountRepository.findByNumberAccount(request.getToAccountNumber()))
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with getToAccountNumber ")))
                .flatMap(accounts -> {
                    Account fromAccount = accounts.getT1();
                    Account toAccount = accounts.getT2();

                    if (fromAccount.getBalance() < request.getAmount()) {
                        return Mono.error(new InsufficientFundsException("Insufficient funds."));
                    }

                    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
                    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

                    return accountRepository.save(fromAccount)
                            .then(accountRepository.save(toAccount))
                            .then(Mono.just(new TransferResponse(request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount())));
                });
    }

    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackTransferExternal")
    @TimeLimiter(name = "bank-account")
    public Mono<TransferResponse> transferExternal(TransferRequest request) {
        return accountRepository.findByNumberAccount(request.getFromAccountNumber())
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with getFromAccountId ")))
                .zipWith(accountRepository.findByNumberAccount(request.getToAccountNumber()))
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not found with getToAccountId ")))
                .flatMap(accounts -> {
                    Account fromAccount = accounts.getT1();
                    Account toAccount = accounts.getT2();

                    if (fromAccount.getClientId().equals(toAccount.getClientId())) {
                        return Mono.error(new AccountException("Cannot transfer to the same client's account"));
                    }

                    if (fromAccount.getBalance() < request.getAmount()) {
                        return Mono.error(new InsufficientFundsException("Insufficient funds."));
                    }

                    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
                    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

                    return accountRepository.save(fromAccount)
                            .then(accountRepository.save(toAccount))
                            .then(Mono.just(new TransferResponse(request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount())));
                });
    }

    @Override
    @CircuitBreaker(name = "bank-account", fallbackMethod = "fallbackGetAccountByClientId")
    @TimeLimiter(name = "bank-account")
    public Flux<AccountResponse> getAccountByClientId(String clientId) {
        return accountRepository.findByClientId(clientId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("account not fetching with clientId ")))
                .map(AccountConverter::toAccountResponse)
                .doOnError(e -> log.error("Error fetching account with Client id: {}", clientId, e))
                .onErrorMap(e -> new Exception("Error fetching account by Client id", e));

    }

    /**
     * Updates the account with a new transaction and converts it to a TransactionResponse.
     *
     * @param transactionMono the Mono emitting the transaction to be added
     * @param account the account to update
     * @return a Mono emitting the TransactionResponse
     */
    private Mono<TransactionResponse> updateTransaction(Mono<Transaction> transactionMono, Account account) {
        return transactionMono.flatMap(transactionToConverter -> {
            if (account.getTransactions() == null) {
                account.setTransactions(new ArrayList<>());
            }
            account.getTransactions().add(transactionToConverter);
            return checker.check(account)
                    .then(accountRepository.save(account))
                    .then(TransactionConverter.toTransactionResponse(transactionToConverter));
        });
    }
    TransactionLimitChecker checker = (Account account) -> {
        if (account.getTransactionCount() > account.getTransactionLimit()) {
            Commission commission = ComissionConverter.createCommissionFromAccount(account);
            log.info("Saving commission for account: {}", account.getId());
            return commissionRepository.save(commission)
                    .doOnSuccess(savedCommission -> log.info("Commission saved: {}", savedCommission))
                    .doOnError(error -> log.error("Error saving commission", error))
                    .then();
        }
        return Mono.empty();
    };

    public Flux<AccountResponse> fallbackGetAllAccounts(Exception exception) {
        log.error("Fallback method for getAllAccounts", exception);
        return Flux.error(new Exception("Fallback method for createAccount"));
    }

    public Mono<AccountResponse> fallbackCreateAccount(Exception exception) {
        log.error("Fallback method for createAccount", exception);
        return Mono.error(new Exception("Fallback method for createAccount"));
    }

    public Mono<AccountResponse> fallbackGetAccountById(Exception exception) {
        log.error("Fallback method for getAccountById", exception);
        return Mono.error(new Exception("Fallback method for getAccountById"));
    }

    public Mono<AccountResponse> fallbackUpdateAccount(Exception exception) {
        log.error("Fallback method for updateAccount", exception);
        return Mono.error(new Exception("Fallback method for updateAccount"));
    }

    public Mono<Void> fallbackDeleteAccount(Exception exception) {
        log.error("Fallback method for deleteAccount", exception);
        return Mono.error(new Exception("Fallback method for deleteAccount"));
    }
    public Mono<TransactionAccountResponse> fallbackGetTransactionByAccount(Exception exception) {
        log.error("Fallback method for getTransactionByAccount", exception);
        return Mono.error(new Exception("Fallback method for getTransactionByAccount"));
    }

    public Mono<TransferResponse> fallbackTransferInternal(Exception exception) {
        log.error("Fallback method for transferInternal", exception);
        return Mono.error(new Exception("Fallback method for transferInternal"));
    }

    public Flux<AccountResponse> fallbackGetAccountByClientId(Exception exception) {
        log.error("Fallback method for getAccountByClientId", exception);
        return Flux.error(new Exception("Fallback method for getAccountByClientId"));
    }

    public Mono<TransferResponse> fallbackTransferExternal(Exception exception) {
        log.error("Fallback method for transferExternal", exception);
        return Mono.error(new Exception("Fallback method for transferExternal"));
    }
    public Flux<BalanceResponse> fallbackGetBalanceByClientId(Exception exception) {
        log.error("Fallback method for getBalanceByClientId", exception);
        return Flux.error(new Exception("Fallback method for getBalanceByClientId"));
    }

    public Mono<TransactionResponse> fallbackDeposit(Exception exception) {
        log.error("Fallback method for deposit", exception);
        return Mono.error(new Exception("Fallback method for deposit"));
    }

    public Mono<TransactionResponse> fallbackWithdraw(Exception exception) {
        log.error("Fallback method for withdraw", exception);
        return Mono.error(new Exception("Fallback method for withdraw"));
    }
    }


