package com.nttdata.bank_account.factory;

import com.nttdata.bank_account.interfaces.AccountTypeInterface;

public class AccountFactory {
    public AccountTypeInterface getAccountType(String accountType) {
        switch (accountType.toUpperCase()) {
            case "SAVINGS":
                return new SavingsAccount();
            case "CURRENT":
                return new CheckingAccount();
            case "FIXED_TERM":
                return new FixedTermAccount();
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }
}
