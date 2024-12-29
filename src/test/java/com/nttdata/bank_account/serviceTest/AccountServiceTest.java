package com.nttdata.bank_account.serviceTest;

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
import com.nttdata.bank_account.service.impl.AccountServiceImpl;
import com.nttdata.bank_account.strategy.ValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @MockBean
    private ClientService clientService;
    @Mock
    private ValidationStrategy validationStrategy;
    @InjectMocks
    private AccountServiceImpl accountService;
    private AccountRequest accountRequest;
    private AccountResponse accountResponse;
    private Account account;
    private Client client;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountRequest = new AccountRequest(AccountType.SAVINGS, 1000.0, "2024-12-21", "12345", List.of("holderId1"), List.of("signerId1"),98231349);
        accountResponse = new AccountResponse();
        account = new Account();
        account.setId("12345");
        account.setType(AccountType.SAVINGS);
        account.setBalance(1000.0);
        account.setOpeningDate(new Date());
        account.setTransactionLimit(5000.0);
        account.setMaintenanceFee(10.0);
        account.setClientId("12345");
        account.setHolders(List.of("holderId1"));
        account.setAuthorizedSigners(List.of("signerId1"));
        client = new Client();
        client.setType(TypeClient.PERSONAL.name());
        accountRequest.setClientId("client-id");
        account.setClientId("client-id");
        validationStrategy = new ValidationStrategy(accountRepository);


    }

    @Test
    public void testGetAllAccounts() {
        when(accountRepository.findAll()).thenReturn(Flux.just(account));

        StepVerifier.create(accountService.getAllAccounts())
                .expectNextMatches(response -> response != null)
                .verifyComplete();
    }
    @Test
    public void testGetAllAccountsError() {
        when(accountRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(accountService.getAllAccounts())
                .expectErrorMatches(throwable -> throwable instanceof Exception && throwable.getMessage().equals("Error fetching all accounts"))
                .verify();
    }
    //Falta validar el client el create
    @Test
    public void testCreateAccount_InvalidClient() {
        Mono<AccountResponse> result = accountService.createAccount(null);

        StepVerifier.create(result)
                .expectError(AccountException.class)
                .verify();
    }

    @Test
    public void testGetAccountById() {
        when(accountRepository.findById("12345")).thenReturn(Mono.just(account));

        StepVerifier.create(accountService.getAccountById("12345"))
                .expectNextMatches(response -> response != null)
                .verifyComplete();
    }

    @Test
    public void testGetAccountByIdError() {
        when(accountRepository.findById("12345")).thenReturn(Mono.error(new AccountException("Account not found")));

        StepVerifier.create(accountService.getAccountById("12345"))
                .expectErrorMatches(throwable -> throwable instanceof Exception && throwable.getMessage().equals("Error fetching account by id"))
                .verify();
    }

    @Test
    public void testUpdateAccount() {
        when(accountRepository.findById("12345")).thenReturn(Mono.just(account));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(account));

        StepVerifier.create(accountService.updateAccount("12345", accountRequest))
                .expectNextMatches(response -> response != null)
                .verifyComplete();
    }

    @Test
    public void testUpdateAccountError() {
        when(accountRepository.findById("12345")).thenReturn(Mono.empty());

        StepVerifier.create(accountService.updateAccount("12345", accountRequest))
                .expectErrorMatches(throwable -> throwable instanceof Exception &&
                        throwable.getMessage().equals("Error updating account"))
                .verify();
    }

    @Test
    public void testDeleteAccount() {

       Account account2 = new Account();
        account2.setId("12346");
        account2.setType(AccountType.SAVINGS);
        account2.setBalance(1000.0);
        account2.setOpeningDate(new Date());
        account2.setTransactionLimit(5000.0);
        account2.setMaintenanceFee(10.0);
        account2.setClientId("12345");
        account2.setHolders(List.of("holderId1"));
        account2.setAuthorizedSigners(List.of("signerId1"));
        when(accountRepository.findById("12345")).thenReturn(Mono.just(account2));
        when(accountRepository.delete(any(Account.class))).thenReturn(Mono.empty());

        StepVerifier.create(accountService.deleteAccount("12345"))
                .verifyComplete();
    }

    @Test
    public void testDeleteAccountError() {
        when(accountRepository.findById("12345")).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(accountService.deleteAccount("12345"))
                .expectErrorMatches(throwable -> throwable instanceof Exception && throwable.getMessage().equals("Error deleting account"))
                .verify();
    }

}
