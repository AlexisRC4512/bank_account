package com.nttdata.bank_account.model.entity;

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
     * Client ID.
     */
    private String clientId;

    /**
     * Credit balance of the client.
     */
    private double creditBalance;

    /**
     * Date of the balance.
     */
    private Date date;
}
