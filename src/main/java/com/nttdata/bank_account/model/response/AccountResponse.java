package com.nttdata.bank_account.model.response;

import com.nttdata.bank_account.model.entity.Transaction;
import com.nttdata.bank_account.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String id;
    private AccountType type;
    private double balance;
    private Date openingDate;
    private double transactionLimit;
    private double maintenanceFee;
    private String clientId;
    private List<String> holders;
    private List<String> authorizedSigners;
    private List<Transaction> transactions;
}
