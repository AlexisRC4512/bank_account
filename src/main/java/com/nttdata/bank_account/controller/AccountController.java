package com.nttdata.bank_account.controller;

import com.nttdata.bank_account.api.ApiApi;
import com.nttdata.bank_account.model.entity.Commission;
import com.nttdata.bank_account.model.request.AccountRequest;
import com.nttdata.bank_account.model.request.TransactionRequest;
import com.nttdata.bank_account.model.request.TransferRequest;
import com.nttdata.bank_account.model.response.*;
import com.nttdata.bank_account.service.AccountService;
import com.nttdata.bank_account.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController implements ApiApi {

    private final AccountService accountService;
    private final CommissionService commissionService;

    @GetMapping
    public Flux<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    public Mono<AccountResponse> createAccount(@RequestBody AccountRequest account , @RequestHeader("Authorization") String authorizationHeader) {
        return accountService.createAccount(account,authorizationHeader);
    }

    @GetMapping("/{id_account}")
    public Mono<AccountResponse> getAccountById(@PathVariable("id_account") String id) {
        return accountService.getAccountById(id);

    }

    @PutMapping("/{id_account}")
    public Mono<AccountResponse> updateAccount(@PathVariable("id_account") String id, @Valid @RequestBody AccountRequest account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping("/{id_account}")
    public Mono<Void> deleteAccount(@PathVariable("id_account") String id) {
        return accountService.deleteAccount(id);

    }
    @PostMapping("/{id_account}/withdraw")
    public Mono<TransactionResponse> withdrawAccount(@PathVariable("id_account") String idAccount,@Valid @RequestBody TransactionRequest transactionRequest) {
        return accountService.withdraw(idAccount,transactionRequest);
    }
    @PostMapping("/{id_account}/deposit")
    public Mono<TransactionResponse> depositAccount(@PathVariable("id_account") String idAccount,@Valid @RequestBody TransactionRequest transactionRequest) {
        return accountService.deposit(idAccount,transactionRequest);
    }
    @GetMapping("/{id_client}/balances")
    public Flux<BalanceResponse> getBalanceAccount(@PathVariable("id_client") String idClient) {
        return accountService.getBalanceByClientId(idClient);
    }
    @GetMapping("/{id_account}/transactions")
    public Mono<TransactionAccountResponse> getTransactionByAccountId(@PathVariable("id_account") String id) {
        return accountService.getTransactionByAccount(id);

    }
    @PostMapping("/internal")
    public Mono<TransferResponse> transferInternal(@RequestBody TransferRequest request) {
        return accountService.transferInternal(request);
    }

    @PostMapping("/external")
    public Mono<TransferResponse> transferExternal(@RequestBody TransferRequest request) {
        return accountService.transferExternal(request);
    }

    @GetMapping("/{id_account}/commissions")
    public Flux<Commission> getCommissionsByAccountIdAndDateRange(
            @PathVariable("id_account") String accountId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return commissionService.getCommissionsByAccountIdAndDateRange(accountId, startDate, endDate);
    }
    @GetMapping("/client/{id_Client}")
    public Flux<AccountResponse> getAccountByClientId(@PathVariable("id_Client") String id) {
        return accountService.getAccountByClientId(id);

    }

}
