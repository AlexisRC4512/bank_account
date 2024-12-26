package com.nttdata.bank_account.model.response;

import com.nttdata.bank_account.model.enums.TypeTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    private String clientId;
    private TypeTransaction type;
    private double amount;
    private Date date;
    private String description;
}
