package com.SkillShare.SkillShare.config;

import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.service.MensagemService;
import com.SkillShare.SkillShare.service.UsuarioService;

import lombok.RequiredArgsConstructor;



// Torna os dados do chat disponiveis para todas as telas renderizadas pelos controllers.
// Assim, as paginas nao precisam buscar manualmente as conversas recentes do usuario logado.
@ControllerAdvice
@RequiredArgsConstructor
public class ChatWidgetAdvice {



    private final MensagemService mensagemService;

    private final UsuarioService usuarioService;



    // Esse metodo roda antes de renderizar as paginas e monta a lista de conversas recentes do usuario.
    @ModelAttribute("conversasWidget")
    public java.util.List<com.SkillShare.SkillShare.Modelo.Mensagem> conversasWidget(Authentication authentication) {

        // Se nao existir usuario logado, o widget recebe lista vazia para nao mostrar conversas indevidas.
        if (authentication == null || !authentication.isAuthenticated()

                || "anonymousUser".equals(authentication.getName())) {

            return Collections.emptyList();

        }

        try {

            // Busca o usuario real do banco a partir dos dados de login do Spring Security.
            Usuario usuario = usuarioService.buscarUsuarioAtual(authentication);

            if (usuario == null || usuario.getIdUsuario() == null) return Collections.emptyList();

            // Carrega a ultima mensagem de cada conversa para preencher a lista lateral/atalho do chat.
            return mensagemService.listarConversas(usuario.getIdUsuario());

        } catch (Exception e) {

            // Qualquer falha no chat nao deve impedir a pagina principal de abrir.
            return Collections.emptyList();

        }
    }


}
