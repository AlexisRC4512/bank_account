package com.nttdata.bank_account.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransferRequest {
    private int fromAccountNumber;
    private int toAccountNumber;
    private double amount;
}
