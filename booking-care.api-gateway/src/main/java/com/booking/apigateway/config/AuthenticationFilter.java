package com.booking.apigateway.config;

import com.booking.apigateway.entities.response.ApiResponse;
import com.booking.apigateway.entities.response.ValidateTokenResponse;
import com.booking.apigateway.usecase.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final TokenService tokenService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final String apiPrefix = "/api/v1";

    private final List<String> publicEndpoints = Arrays.asList(
            "/auth/**",
            "/account/**",
            "/ratings/**"
    );

    private final List<String> adminEndpoints = Arrays.asList(
            "/admin/users/**",
            "/admin/permissions/**",
            "/admin/roles/**",
            "/ratings/**"
    );

    private final List<String> userEndpoints = Arrays.asList(
            "/users/**",
            "/ratings/**"
    );

    private final List<String> doctorEndpoints = Arrays.asList(
            "/doctors/**"
    );

    private final List<String> accountantEndpoints = Arrays.asList(
            "/accountant/**"
    );

    private final List<String> protocolEndpoints = Arrays.asList(
            "/protocol/**"
    );


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicEndpoint(request)) {
            return chain.filter(exchange);
        }

        Optional<String> tokenOptional = extractToken(request);
        return tokenOptional.map(token ->
                tokenService.validToken(token)
                        .flatMap(tokenResponse -> {
                            if (!tokenResponse.isValid()) {
                                return unauthenticated(exchange.getResponse());
                            }
                            if (isAdminEndpoint(request) && !isAdmin(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            if (isUserEndpoint(request) && !isUser(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            if (isDoctorEndpoint(request) && !isDoctor(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            if (isAccountantEndpoint(request) && !isAccountant(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            if (isProtocolEndpoint(request) && !isProtocol(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> handleError(exchange.getResponse()))
        ).orElseGet(() -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return publicEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isAdminEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return adminEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isUserEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return userEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isDoctorEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return doctorEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(endpoint, path));
    }

    private boolean isAccountantEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return accountantEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isProtocolEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return protocolEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isAdmin(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("ADMIN");
    }

    private boolean isDoctor(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("DOCTOR");
    }

    private boolean isAccountant(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("ACCOUNTANT");
    }

    private boolean isProtocol(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("PROTOCOL");
    }

    private boolean isUser(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("USER");
    }

    private Optional<String> extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return Optional.ofNullable(authHeader)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.replace("Bearer ", ""));
    }

    private Mono<Void> unauthenticated(ServerHttpResponse response) {
        return createErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthenticated");
    }

    private Mono<Void> unauthorized(ServerHttpResponse response) {
        return createErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden");
    }

    private Mono<Void> createErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(message)
                .build();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(apiResponse.toString().getBytes(StandardCharsets.UTF_8))));
    }


    @SuppressWarnings("unchecked")
    private <T> Mono<T> handleError(ServerHttpResponse response) {
        // Log the error for debugging purposes
        // logger.error("Error occurred: {}", e.getMessage());
        return (Mono<T>) createErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}
