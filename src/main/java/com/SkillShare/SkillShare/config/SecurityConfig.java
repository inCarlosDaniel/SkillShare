package com.SkillShare.SkillShare.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define as regras principais de seguranca HTTP: CSRF, permissoes de URL, login, logout e OAuth2.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
            OAuth2LoginSucessoHandler oauth2LoginSucessoHandler,
            OAuth2UsuarioService oauth2UsuarioService) throws Exception {

        httpSecurity
                // Protege formularios contra CSRF e libera excecoes necessarias para cadastro, verificacao e WebSocket.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())

                        .ignoringRequestMatchers("/usuarios", "/usuarios/verificacao/confirmar", "/ws/**"))

                // Define quais rotas sao publicas e quais exigem usuario autenticado.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/feed", "/busca", "/grupos", "/login", "/cadastro", "/css/**", "/js/**", "/images/**", "/animations/**", "/oauth2/**", "/login/oauth2/**", "/ws/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/materiais-repositorio", "/materiais-repositorio/download/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/usuarios", "/usuarios/verificacao/confirmar").permitAll()

                        .requestMatchers(HttpMethod.POST, "/materiais-repositorio").authenticated()

                        .requestMatchers("/perfil", "/perfil-aprendizado", "/validando-informacoes", "/match", "/notificacoes", "/mensagens-lista", "/configuracao-conta", "/chat/**", "/mensagens/**", "/postagens/**", "/materiais/**", "/anexos/**", "/grupos-estudo/**", "/usuario/**").authenticated()

                        .anyRequest().authenticated())

                // Configura o login por formulario usando a pagina personalizada do projeto.
                .formLogin(form -> form
                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .defaultSuccessUrl("/", true)

                        .failureUrl("/login?erro")

                        .permitAll())

                // Configura a saida do usuario e o redirecionamento apos logout.
                .logout(logout -> logout
                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout")

                        .permitAll());

        // Habilita login OAuth2 somente quando existir provedor configurado, evitando erro em ambientes sem GitHub/Google.
        if (clientRegistrationRepository.getIfAvailable() != null) {

            httpSecurity.oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")

                    .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UsuarioService))

                    .successHandler(oauth2LoginSucessoHandler));
        }

        return httpSecurity.build();
    }

    // Cria o codificador BCrypt usado para salvar e conferir senhas sem guardar texto puro.
    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
