package com.nttdata.bank_account.service;

import com.nttdata.bank_account.model.entity.CreditCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class CreditCardService {

    @Autowired
    private WebClient webClient;

    public Flux<CreditCard> getClientById(String clientId) {
        return webClient.get()
                .uri("/api/v1/CreditCard/{id_client}/client", clientId)
                .retrieve()
                .bodyToFlux(CreditCard.class);
    }
}
