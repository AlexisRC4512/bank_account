package com.nttdata.bank_account.model.entity;

import com.nttdata.bank_account.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * Represents an account in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account")
public class Account {
    /**
     * Unique identifier for the account.
     */
    @Id
    private String id;

    /**
     * Type of the account.
     */
    private AccountType type;

    /**
     * Balance of the account.
     */
    private double balance;

    /**
     * Opening date of the account.
     */
    private Date openingDate;

    /**
     * Transaction limit of the account.
     */
    private double transactionLimit;

    /**
     * Maintenance fee of the account.
     */
    private double maintenanceFee;

    /**
     * Client ID associated with the account.
     */
    private String clientId;

    /**
     * List of account holders.
     */
    private List<String> holders;

    /**
     * List of authorized signers for the account.
     */
    private List<String> authorizedSigners;

    /**
     * List of transactions associated with the account.
     */
    private List<Transaction> transactions;
}
