package com.booking.apigateway.config;

import com.booking.apigateway.respositories.ValidateTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebclientConfig {
    @Bean
    WebClient webClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:5004/auth/validate-token")
                .build();
    }

    @Bean
    ValidateTokenRepository validateTokenRepository(WebClient webClient){
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(ValidateTokenRepository.class);
    }
}
