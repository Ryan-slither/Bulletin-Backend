package com.bulletin.thing.socket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.bulletin.thing.model.TokenDTO;

import java.util.Map;

@Component
public class SocketHandshakeInterceptor implements HandshakeInterceptor {

    private final RestTemplate restTemplate;

    public SocketHandshakeInterceptor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @Nullable WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {

        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {

            String token = query.split("token=")[1];
            if (token.isEmpty()) {

                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                throw new HandshakeFailureException("No JWT token");

            }

            TokenDTO tokenDTO = new TokenDTO(token);

            try {
                ResponseEntity<String> userId = restTemplate.postForEntity(
                        "http://users/api/v1/users/verify-token",
                        tokenDTO,
                        String.class);

                if (userId.getStatusCode() == HttpStatus.UNAUTHORIZED) {

                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    throw new HandshakeFailureException("Invalid JWT token");

                }

                attributes.put("userId", userId.getBody());
                System.out.println("Handshake complete with user ID: " + userId);

            } catch (RestClientException ex) {

                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                throw new HandshakeFailureException("Token verification failed", ex);

            }

            return true;

        }

        return false;

    }

    @Override
    public void afterHandshake(@Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response,
            @Nullable WebSocketHandler wsHandler, @Nullable Exception exception) {
        System.out.println("Hands Have Been Shaked");
        if (exception != null) {
            System.err.println("Handshake failed: " + exception.getMessage());
        }
    }
}