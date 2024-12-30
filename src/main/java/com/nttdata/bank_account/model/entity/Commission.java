package com.nttdata.bank_account.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "commission")
public class Commission {
    @Id
    private String id;
    private String accountId;
    private String clientId;
    private double amount;
    private Date date;
    private String description;
}
