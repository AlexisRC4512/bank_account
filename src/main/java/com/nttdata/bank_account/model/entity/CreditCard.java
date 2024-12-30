package com.nttdata.bank_account.model.entity;

import java.util.Date;
import java.util.List;

public class CreditCard {
    private String id;
    private String type;
    private double creditLimit;
    private double availableBalance;
    private Date issueDate;
    private Date expirationDate;
    private String clientId;
    private List<Transaction> transactions;
}
