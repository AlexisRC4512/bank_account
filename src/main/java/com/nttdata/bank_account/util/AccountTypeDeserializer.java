package com.nttdata.bank_account.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nttdata.bank_account.model.enums.AccountType;

import java.io.IOException;

public class AccountTypeDeserializer extends JsonDeserializer<AccountType> {
    @Override
    public AccountType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase();
        return AccountType.valueOf(value);
    }
}
