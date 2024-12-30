package com.nttdata.bank_account.model.entity;

import com.nttdata.bank_account.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Represents the balance of a client.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Balance {
    /**
     * Account ID.
     */
    private String accountId;

    /**
     * Credit balance of the client.
     */
    private double creditBalance;

    /**
     * Date of the balance.
     */
    private Date date;
    /**
     * Type of the account.
     */
    private AccountType accountType;

    public Balance(String id, double balance) {
    }


}
