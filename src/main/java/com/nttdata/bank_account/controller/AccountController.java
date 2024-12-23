package com.nttdata.bank_account.controller;

import com.nttdata.bank_account.model.request.AccountRequest;
import com.nttdata.bank_account.model.response.AccountResponse;
import com.nttdata.bank_account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public Flux<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    public Mono<AccountResponse> createAccount(@RequestBody AccountRequest account) {
        return accountService.createAccount(account);
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
        accountService.deleteAccount(id);
        return accountService.deleteAccount(id);

    }
}
