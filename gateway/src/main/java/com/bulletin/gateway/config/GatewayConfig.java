package com.bulletin.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("users-service", r -> r
                                                .path("/api/v1/users/**")
                                                .uri("lb://users"))
                                .route("userlike-service", r -> r
                                                .path("/api/v1/userlike/**")
                                                .uri("lb://userlike"))
                                .route("bulletin-service", r -> r
                                                .path("/api/v1/bulletin/**")
                                                .uri("lb://bulletin"))
                                .route("membership-service", r -> r
                                                .path("/api/v1/membership/**")
                                                .uri("lb://membership"))
                                .route("thing-service", r -> r
                                                .path("/api/v1/thing/**")
                                                .uri("lb://thing"))
                                .route("thing-stomp", r -> r
                                                .path("/ws/thing/**")
                                                .and()
                                                .header("Upgrade", "websocket")
                                                .uri("lb://thing"))
                                .route("userlike-stomp", r -> r
                                                .path("/ws/userlike/**")
                                                .and()
                                                .header("Upgrade", "websocket")
                                                .uri("lb://userlike"))
                                .build();
        }

        @Bean
        @LoadBalanced
        public WebClient.Builder webClientBuilder() {
                return WebClient.builder();
        }

}
