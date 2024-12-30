package com.nttdata.bank_account.interfaces;

import com.nttdata.bank_account.model.entity.Account;
/**
 * Interface for applying account-specific rules.
 */
public interface AccountTypeInterface {
    /**
     * Applies the rules specific to the account type.
     *
     * @param account the account to which the rules will be applied
     */
    void applyAccountRules(Account account);
}
