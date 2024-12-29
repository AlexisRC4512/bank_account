package com.nttdata.bank_account.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nttdata.bank_account.model.enums.AccountType;
import com.nttdata.bank_account.util.AccountTypeDeserializer;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class AccountRequest {
    @JsonDeserialize(using = AccountTypeDeserializer.class)
    private AccountType type;
    private double balance;
    private String openingDate;
    private String clientId;
    private List<String> holders;
    private List<String> authorizedSigners;
    private Integer numberAccount;

    public AccountRequest(AccountType type, double balance, String openingDate, String clientId,List<String> holders,List<String> authorizedSigners,Integer numberAccount) {
        setType(type);
        setBalance(balance);
        setOpeningDate(openingDate);
        setClientId(clientId);
        setAuthorizedSigners(authorizedSigners);
        setHolders(holders);
        setNumberAccount(numberAccount);
    }

    public void setNumberAccount(Integer numberAccount) {
        if (numberAccount.toString().length() < 8) {
            throw new IllegalArgumentException("The account number must have at least 8 characters.");        }
        this.numberAccount = numberAccount;
    }

    public void setHolders(List<String> holders) {
        if (holders == null || holders.isEmpty()) {
            holders = new ArrayList<>();
            holders.add(getClientId());
        }
        this.holders = holders;
    }

    public void setAuthorizedSigners(List<String> authorizedSigners) {
        if (authorizedSigners == null) {
            authorizedSigners = new ArrayList<>();
        }
        this.authorizedSigners = authorizedSigners;
    }
    public void setType(AccountType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is required");
        }
        this.type = type;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance must be non-negative");
        }
        this.balance = Math.round(balance);
    }

    public void setOpeningDate(String openingDate) {
        if (openingDate == null || openingDate.isEmpty()) {
            throw new IllegalArgumentException("Opening date is required");
        }
        this.openingDate = openingDate;
    }


    public void setClientId(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Client ID is required");
        }
        this.clientId = clientId;
    }
}
