package com.nttdata.bank_account.model.request;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class TransactionRequest {
    private Double amount;

    public TransactionRequest(Double amount) {
        setAmount(amount);
    }

    public void setAmount(Double amount) {
        if (amount<0 || amount==null){
            throw new IllegalArgumentException("amount is null or amount less than 0");
        }
        this.amount = amount;
    }
}
