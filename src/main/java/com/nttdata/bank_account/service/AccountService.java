package com.nttdata.bank_account.service;

import com.nttdata.bank_account.model.request.AccountRequest;
import com.nttdata.bank_account.model.request.TransactionRequest;
import com.nttdata.bank_account.model.response.AccountResponse;
import com.nttdata.bank_account.model.response.BalanceResponse;
import com.nttdata.bank_account.model.response.TransactionAccountResponse;
import com.nttdata.bank_account.model.response.TransactionResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface AccountService {
    Flux<AccountResponse> getAllAccounts();
    Mono<AccountResponse> createAccount(AccountRequest account);
    Mono<AccountResponse> getAccountById(String id);
    Mono<AccountResponse> updateAccount(String id, AccountRequest account);
    Mono<Void> deleteAccount(String id);
    Mono<TransactionResponse> withdraw(String idAccount ,TransactionRequest transactionRequest);
    Mono<TransactionResponse> deposit(String idAccount ,TransactionRequest transactionRequest);
    Flux<BalanceResponse> getBalanceByClientId(String idClient);
    Mono<TransactionAccountResponse>getTransactionByAccount(String idAccount);

}
