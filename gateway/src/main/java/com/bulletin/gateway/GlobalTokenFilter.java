package com.bulletin.gateway;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GlobalTokenFilter implements GlobalFilter {

    private final List<String> PUBLIC_PATHS = List.of("/api/v1/users/sign-in", "/api/v1/users/",
            "/actuator/gateway/routes", "/api/v1/users/verify");

    private final WebClient client;

    private static final Logger log = LoggerFactory.getLogger(GlobalTokenFilter.class);

    public GlobalTokenFilter(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();

        if ("websocket".equalsIgnoreCase(req.getHeaders().getUpgrade())) {
            log.info("This is a WS Request");
        }

        String requestURI = req.getURI().getPath().toString();
        String path = requestURI.endsWith("/") ? requestURI : requestURI + "/";
        boolean isPublic = PUBLIC_PATHS.stream()
                .map(p -> p.endsWith("/") ? p : p + "/")
                .anyMatch(path::endsWith);

        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = "";

        if (authHeader != null) {
            token = authHeader.substring(7);
        }

        if (!token.isEmpty()) {
            return client.post()
                    .uri("http://users/api/v1/users/verify-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new TokenDTO(token))
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(userId -> {
                        log.info("✅ Verified JWT for userId={}", userId);

                        ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .build();

                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(mutatedReq)
                                .build();

                        return chain.filter(mutatedExchange);
                    })
                    .onErrorResume(err -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

}
