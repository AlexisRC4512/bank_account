package com.nttdata.bank_account.factory;

import com.nttdata.bank_account.interfaces.AccountTypeInterface;
/**
 * Factory class for creating instances of different account types.
 */
public class AccountFactory {
    /**
     * Returns an instance of an account type based on the provided account type string.
     *
     * @param accountType the type of account to create (e.g., "SAVINGS", "CURRENT", "FIXED_TERM")
     * @return an instance of AccountTypeInterface corresponding to the specified account type
     * @throws IllegalArgumentException if the account type is unknown
     */
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
