package com.nttdata.bank_account.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private int fromAccountNumber;
    private int toAccountNumber;
    private double amount;
}
