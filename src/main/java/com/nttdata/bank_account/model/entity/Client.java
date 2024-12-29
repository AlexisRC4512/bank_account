package com.nttdata.bank_account.model.entity;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String name;
    private String type;
    private int documentNumber;
    private String address;
    private String phone;
    private String email;
    private String subType;
}
