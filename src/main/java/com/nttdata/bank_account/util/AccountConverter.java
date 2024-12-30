package com.nttdata.bank_account.util;

import com.nttdata.bank_account.factory.AccountFactory;
import com.nttdata.bank_account.interfaces.AccountTypeInterface;
import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.enums.AccountType;
import com.nttdata.bank_account.model.request.AccountRequest;
import com.nttdata.bank_account.model.response.AccountResponse;

import java.util.Date;


public class AccountConverter {
    public static AccountResponse toAccountResponse(Account account) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(account.getId());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setHolders(account.getHolders());
        accountResponse.setType(AccountType.valueOf(account.getType().name()));
        accountResponse.setTransactionLimit(account.getTransactionLimit());
        accountResponse.setMaintenanceFee(account.getMaintenanceFee());
        accountResponse.setOpeningDate(account.getOpeningDate());
        accountResponse.setClientId(account.getClientId());
        accountResponse.setAuthorizedSigners(account.getAuthorizedSigners());
        accountResponse.setTransactions(account.getTransactions());
        accountResponse.setNumberAccount(account.getNumberAccount());
        accountResponse.setTransactionCount(account.getTransactionCount());
        return accountResponse;
    }

    public static Account toAccount(AccountRequest request) {
        AccountFactory accountFactory = new AccountFactory();
        Account account = new Account();
        account.setType(request.getType());
        account.setBalance(request.getBalance());
        account.setOpeningDate(new Date());
        account.setClientId(request.getClientId());
        account.setNumberAccount(request.getNumberAccount());
        account.setHolders(request.getHolders());
        account.setTransactionCount(request.getTransactionCount());
        account.setAuthorizedSigners(request.getAuthorizedSigners());
        AccountTypeInterface accountType = accountFactory.getAccountType(request.getType().name());
        accountType.applyAccountRules(account);
        return account;
    }
}
