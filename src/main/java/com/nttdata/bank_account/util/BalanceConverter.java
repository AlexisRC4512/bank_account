package com.nttdata.bank_account.util;

import com.nttdata.bank_account.model.entity.Account;
import com.nttdata.bank_account.model.entity.Balance;
import com.nttdata.bank_account.model.response.BalanceResponse;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BalanceConverter {
    public static BalanceResponse toBalanceResponse(List<Account> accounts) {
        BalanceResponse balanceResponse = new BalanceResponse();
        List<Balance> listBalances = accounts.stream()
                .map(account -> new Balance(account.getId(), account.getBalance(), new Date(), account.getType()))
                .collect(Collectors.toList());
        balanceResponse.setBalances(listBalances);
        if (!accounts.isEmpty()) {
            balanceResponse.setClientId(accounts.get(0).getClientId());
        }
        return balanceResponse;
    }

}
