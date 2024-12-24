package com.nttdata.bank_account.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:8085")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
