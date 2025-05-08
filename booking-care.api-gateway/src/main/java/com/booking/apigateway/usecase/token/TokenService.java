package com.booking.apigateway.usecase.token;

import com.booking.apigateway.entities.request.ValidateTokenRequest;
import com.booking.apigateway.entities.response.ValidateTokenResponse;
import com.booking.apigateway.respositories.ValidateTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final ValidateTokenRepository validateTokenRepository;

    public Mono<ValidateTokenResponse> validToken(String token){
        return validateTokenRepository.validateToken(ValidateTokenRequest.builder()
                .token(token)
                .build());
    }
}
