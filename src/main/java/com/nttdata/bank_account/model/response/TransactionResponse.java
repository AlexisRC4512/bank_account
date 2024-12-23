package com.nttdata.bank_account.model.response;

import com.nttdata.bank_account.model.entity.Transaction;
import com.nttdata.bank_account.model.enums.TypeTransaction;

import java.util.Date;
import java.util.List;

public class TransactionResponse {

    private TypeTransaction type;
    private Double amount;
    private Date date;
    private List<Transaction> transactions;
}
