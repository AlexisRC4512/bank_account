package com.nttdata.bank_account.service;

import com.nttdata.bank_account.model.entity.CreditCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private  final WebClient webClient;

    public Flux<CreditCard> getClientById(String clientId ,String authorizationHeader) {
        return webClient.get()
                .uri("/api/v1/CreditCard/{id_client}/client", clientId)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToFlux(CreditCard.class);
    }
}
