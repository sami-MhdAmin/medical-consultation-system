package com.grad.akemha.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws");
        registry.addEndpoint("/ws").setAllowedOrigins("*")
                .withSockJS(); // connect to here
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
        System.out.println("sami in configureMessageBroker");
    }

    @EventListener
    public void onDisconnectedEvent(SessionDisconnectEvent event){
        log.info("client with session id {} disconnected " + event.getSessionId());
    }

    @EventListener
    public void onConnectEvent(SessionConnectEvent event){
        log.info("client with session id {} connect " + event.getTimestamp());
    }

    @EventListener
    public void onConnectedEvent(SessionConnectedEvent event){
        log.info("client with session id {} connected " + event.getTimestamp());
    }
}
