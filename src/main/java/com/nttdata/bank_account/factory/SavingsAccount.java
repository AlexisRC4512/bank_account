package com.nttdata.bank_account.factory;

import com.nttdata.bank_account.interfaces.AccountTypeInterface;
import com.nttdata.bank_account.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@AllArgsConstructor
@Slf4j
public class SavingsAccount implements AccountTypeInterface {
    @Override
    public void applyAccountRules(Account account) {
        account.setMaintenanceFee(0);
        account.setTransactionLimit(10);
        log.info("Savings Account rules applied.");
    }
}
