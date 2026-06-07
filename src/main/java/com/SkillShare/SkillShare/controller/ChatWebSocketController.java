package com.SkillShare.SkillShare.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.SkillShare.SkillShare.Modelo.Mensagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.MensagemService;
import com.SkillShare.SkillShare.service.PresencaService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final SimpMessagingTemplate messagingTemplate;

    private final MensagemService mensagemService;

    private final UsuarioService usuarioService;

    private final PresencaService presencaService;

    // Recebe, salva e entrega mensagens do chat via WebSocket
    @MessageMapping("/chat.enviar")

    public void enviarMensagem(MensagemWsRequest req, Principal principal) {

        Usuario remetente = usuarioService.buscarPorEmailOuNome(principal.getName());

        Usuario destinatario = usuarioService.buscarPorId(req.idDestinatario());

        if (remetente == null || destinatario == null) {

            log.warn("Mensagem WebSocket ignorada: remetente ou destinatario nao encontrado");

            return;
        }

        Mensagem mensagem = new Mensagem();

        mensagem.setTexto(req.texto());

        mensagem.setDataEnvio(LocalDateTime.now());

        mensagem.setLida(false);

        mensagem.setRemetente(remetente);

        mensagem.setDestinatario(destinatario);

        mensagemService.cadastrarMensagem(mensagem);

        String hora = mensagem.getDataEnvio().format(HH_MM);

        int remId = remetente.getIdUsuario();

        messagingTemplate.convertAndSendToUser(

                destinatario.getEmail(),

                "/queue/mensagens",

                new MensagemWsResponse(remetente.getNome(), req.texto(), hora, false, remId,
                        destinatario.getIdUsuario()));

        messagingTemplate.convertAndSendToUser(

                remetente.getEmail(),

                "/queue/mensagens",

                new MensagemWsResponse(remetente.getNome(), req.texto(), hora, true, remId,
                        destinatario.getIdUsuario()));
    }

    // Notifica o destinatario quando o usuario esta digitando
    @MessageMapping("/chat.digitando")

    public void notificarDigitando(DigitandoRequest req, Principal principal) {

        Usuario remetente = usuarioService.buscarPorEmailOuNome(principal.getName());

        Usuario destinatario = usuarioService.buscarPorId(req.idDestinatario());

        if (remetente == null || destinatario == null)
            return;

        messagingTemplate.convertAndSendToUser(

                destinatario.getEmail(),
                "/queue/digitando",
                new DigitandoNotification(remetente.getNome()));
    }

    // Verifica se um contato esta online e registra quem acompanha essa presenca
    @MessageMapping("/chat.verificarPresenca")

    public void verificarPresenca(PresencaRequest req, Principal principal) {

        Usuario remetente = usuarioService.buscarPorEmailOuNome(principal.getName());

        Usuario destinatario = usuarioService.buscarPorId(req.idDestinatario());

        if (remetente == null || destinatario == null)
            return;

        presencaService.adicionarWatcher(destinatario.getEmail(), remetente.getEmail());

        boolean online = presencaService.estaOnline(destinatario.getEmail());

        messagingTemplate.convertAndSendToUser(

                remetente.getEmail(),

                "/queue/presenca",

                new PresencaResponse(online));
    }

    record MensagemWsRequest(String texto, int idDestinatario) {
    }

    record MensagemWsResponse(String nome, String texto, String hora, boolean proprio, int remetenteId,
            int destinatarioId) {
    }

    record DigitandoRequest(int idDestinatario) {
    }

    record DigitandoNotification(String nome) {
    }

    record PresencaRequest(int idDestinatario) {
    }

    record PresencaResponse(boolean online) {
    }

}
