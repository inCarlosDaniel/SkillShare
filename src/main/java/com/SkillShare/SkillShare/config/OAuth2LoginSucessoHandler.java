package com.SkillShare.SkillShare.config; 

import java.io.IOException; 

import org.springframework.security.core.Authentication; 
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken; 
import org.springframework.security.oauth2.core.user.OAuth2User; 
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; 
import org.springframework.stereotype.Component; 

import com.SkillShare.SkillShare.service.UsuarioService; 

import jakarta.servlet.ServletException; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 

import lombok.RequiredArgsConstructor; 

@Component // Registra esta classe como componente gerenciado pelo Spring
@RequiredArgsConstructor
public class OAuth2LoginSucessoHandler implements AuthenticationSuccessHandler { // Implementa o handler executado após login OAuth2 bem-sucedido

    private final UsuarioService usuarioService; //cadastrar automaticamente novos usuários OAuth2

    @Override // Sobrescreve o método da interface AuthenticationSuccessHandler
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException { // Método chamado pelo Spring Security após autenticação OAuth2 concluída com sucesso

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) { // Verifica se a autenticação é do tipo OAuth2; caso contrário, redireciona para erro

            response.sendRedirect("/login?erro"); // Redireciona para a página de login com indicador de erro se o token não for OAuth2

            return; // Encerra o método após o redirecionamento de erro
        }

        OAuth2User oauthUser = oauthToken.getPrincipal(); // Obtém o objeto do usuário OAuth2 com todos os atributos fornecidos pelo provedor

        String email = oauthUser.getAttribute("email"); // Extrai o atributo de e-mail dos dados fornecidos pelo provedor OAuth2

        if (email == null || email.isBlank()) { // Verifica se o e-mail foi obtido corretamente

            response.sendRedirect("/login?erro"); // Redireciona para erro se o e-mail não estiver disponível

            return; // Encerra o método após o redirecionamento de erro
        }

        String nome = oauthUser.getAttribute("name"); // Tenta extrair o nome completo do usuário dos atributos do provedor

        if (nome == null || nome.isBlank()) { // Verifica se o atributo "name" está disponível

            nome = oauthUser.getAttribute("login"); // Usa o atributo "login" como fallback (usado pelo GitHub)
        }

        if (nome == null || nome.isBlank()) { // Verifica se algum nome foi obtido até agora

            nome = email; // Usa o próprio e-mail como nome de exibição se nenhum nome estiver disponível
        }

        boolean novoUsuario = usuarioService.cadastrarUsuarioOAuthSeNaoExistir(nome, email); // Cadastra o usuário automaticamente se for a primeira vez, retornando true se foi criado

        response.sendRedirect(novoUsuario ? "/perfil-aprendizado" : "/"); // Redireciona novos usuários para configurar o perfil de aprendizado; os existentes vão para a home
    }
}
