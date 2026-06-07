package com.SkillShare.SkillShare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Configura os caminhos usados pelo STOMP para enviar mensagens do chat.
    // /queue e usado para mensagens privadas; /topic pode ser usado para avisos coletivos.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/queue", "/topic");

        // Tudo que o front-end envia para /app e encaminhado para metodos @MessageMapping no backend.
        config.setApplicationDestinationPrefixes("/app");

        // Prefixo usado pelo Spring para entregar mensagens privadas para um usuario especifico.
        config.setUserDestinationPrefix("/user");
    }

    // Registra o endpoint que o front-end usa para abrir a conexao WebSocket.
    // SockJS fica habilitado como fallback para navegadores ou ambientes sem WebSocket nativo.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws").withSockJS();
    }
}
