package com.nttdata.bank_account.interfaces;

import com.nttdata.bank_account.model.entity.Account;

public interface AccountTypeInterface {
    void applyAccountRules(Account account);
}
