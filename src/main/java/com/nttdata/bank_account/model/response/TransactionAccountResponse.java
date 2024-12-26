package com.nttdata.bank_account.model.response;

import com.nttdata.bank_account.model.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionAccountResponse {
    private List<Transaction> transactions;
}
