package com.nttdata.bank_account.service;

import com.nttdata.bank_account.model.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Service
public class ClientService {
    @Autowired
    private WebClient webClient;

    public Mono<Client> getClientById(String clientId) {
        return webClient.get()
                .uri("/api/v1/client/{id}", clientId)
                .retrieve()
                .bodyToMono(Client.class);
    }
}
