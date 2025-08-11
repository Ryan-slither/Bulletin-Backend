package com.bulletin.thing.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final SocketHandshakeInterceptor handshakeInterceptor;

  public WebSocketConfig(SocketHandshakeInterceptor handshakeInterceptor) {
    this.handshakeInterceptor = handshakeInterceptor;
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws/thing")
        .setAllowedOriginPatterns("*")
        .addInterceptors(handshakeInterceptor);
  }

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
    config
        .setApplicationDestinationPrefixes("/app")
        .enableSimpleBroker("/topic");
  }

}
