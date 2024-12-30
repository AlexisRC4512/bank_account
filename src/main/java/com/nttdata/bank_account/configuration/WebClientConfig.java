package com.nttdata.bank_account.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Configuration class for setting up a WebClient bean.
 */
@Configuration
public class WebClientConfig {

    @Value("${server.url.client}")
    private String clientUrl;

    @Value("${server.url.credit.card}")
    private String creditCardUrl;
    /**
     * Creates and configures a WebClient bean.
     *
     * @return a configured WebClient instance
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(clientUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    @Bean
    public WebClient webClientCreditCard() {
        return WebClient.builder()
                .baseUrl(creditCardUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
