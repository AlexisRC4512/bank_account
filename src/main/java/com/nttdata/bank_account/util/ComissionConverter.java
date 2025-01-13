package com.nttdata.bank_account.util;

import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.entity.Commission;

import java.util.UUID;

public class ComissionConverter {
    public static Commission createCommissionFromAccount(Account account) {
        Commission commission = new Commission();
        commission.setId(UUID.randomUUID().toString());
        commission.setAccountId(account.getId());
        commission.setClientId(account.getClientId());
        commission.setAmount(account.getTransactionLimit());
        commission.setDate(account.getOpeningDate());
        commission.setDescription("Commission based on transaction limit");
        return commission;
    }
}
