package com.nttdata.bank_account.factory;

import com.nttdata.bank_account.interfaces.AccountTypeInterface;
import com.nttdata.bank_account.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Implementation of AccountTypeInterface for fixed term accounts.
 */
@AllArgsConstructor
@Slf4j
public class FixedTermAccount implements AccountTypeInterface {
    /**
     * Applies the rules specific to fixed term accounts.
     *
     * @param account the account to which the rules will be applied
     */
    @Override
    public void applyAccountRules(Account account) {
        account.setMaintenanceFee(0);
        account.setTransactionLimit(1);
        log.info("Fixed Term Account rules applied.");
    }
}
