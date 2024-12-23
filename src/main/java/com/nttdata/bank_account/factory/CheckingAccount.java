package com.nttdata.bank_account.factory;

import com.nttdata.bank_account.interfaces.AccountTypeInterface;
import com.nttdata.bank_account.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@AllArgsConstructor
@Slf4j
public class CheckingAccount implements AccountTypeInterface {
    @Override
    public void applyAccountRules(Account account) {
        account.setMaintenanceFee(10);
        account.setTransactionLimit(Integer.MAX_VALUE);
        log.info("Checking Account rules applied.");
    }
}
