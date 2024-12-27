package com.nttdata.bank_account.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Configuration class for setting up a WebClient bean.
 */
@Configuration
public class WebClientConfig {
    /**
     * Creates and configures a WebClient bean.
     *
     * @return a configured WebClient instance
     */
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:8085")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
