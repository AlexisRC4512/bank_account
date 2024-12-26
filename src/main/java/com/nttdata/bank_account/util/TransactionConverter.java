package com.nttdata.bank_account.util;

import com.nttdata.bank_account.model.entity.Transaction;
import com.nttdata.bank_account.model.enums.TypeTransaction;
import com.nttdata.bank_account.model.request.TransactionRequest;
import com.nttdata.bank_account.model.response.TransactionResponse;
import reactor.core.publisher.Mono;

import java.util.Date;

public class TransactionConverter{
    public static Mono<Transaction> toTransaction(TransactionRequest transactionRequest, String clientId, TypeTransaction type, String description) {
        if (transactionRequest == null) {
            return Mono.error(new IllegalArgumentException("TransactionRequest is null"));
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setClientId(clientId);
        transaction.setType(type);
        transaction.setDate(new Date());
        transaction.setDescription(description);
        return Mono.just(transaction);
    }

    public static Mono<TransactionResponse> toTransactionResponse(Transaction transaction) {
        if (transaction == null) {
            return Mono.error(new IllegalArgumentException("Transaction is null"));
        }
        TransactionResponse transactionResponse = new TransactionResponse(
                transaction.getClientId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getDescription()
        );
        return Mono.just(transactionResponse);
    }

}
