package com.SkillShare.SkillShare.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.SkillShare.SkillShare.service.PresencaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConexaoWebSocketListener {

    private final PresencaService presencaService;

    private final SimpMessagingTemplate messagingTemplate;

    // Executa automaticamente quando um usuario autenticado abre uma conexao WebSocket.
    // Esse evento e usado para marcar o usuario como online e avisar quem esta observando seu status.
    @EventListener
    public void onConnect(SessionConnectedEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) return;

        String email = accessor.getUser().getName();

        presencaService.registrarOnline(email);

        log.debug("Usuário conectado: {}", email);

        notificarWatchers(email, true);
    }

    // Executa automaticamente quando a conexao WebSocket e encerrada.
    // Antes de remover a presenca, avisa os observadores que o usuario ficou offline.
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) return;

        String email = accessor.getUser().getName();

        notificarWatchers(email, false);

        presencaService.registrarOffline(email);

        log.debug("Usuário desconectado: {}", email);
    }

    // Envia pelo WebSocket uma atualizacao de presenca para todos que acompanham o usuario observado.
    private void notificarWatchers(String emailObservado, boolean online) {

        var watcherEmails = presencaService.getWatchers(emailObservado);

        if (watcherEmails.isEmpty()) return;

        var resposta = new PresencaResponse(online);

        for (String watcher : watcherEmails) {

            // Envia a resposta para a fila privada do usuario que esta observando o status.
            messagingTemplate.convertAndSendToUser(watcher, "/queue/presenca", resposta);
        }
    }

    // Objeto simples enviado ao front-end informando se o usuario esta online ou offline.
    record PresencaResponse(boolean online) {}
}
