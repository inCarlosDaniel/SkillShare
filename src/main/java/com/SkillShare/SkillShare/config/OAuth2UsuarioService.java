package com.SkillShare.SkillShare.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OAuth2UsuarioService extends DefaultOAuth2UserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Carrega os dados do usuario autenticado por OAuth2.
    // Para provedores como Google, normalmente o e-mail ja vem nos atributos padrao.
    // Para GitHub, o e-mail pode vir vazio; nesse caso, o metodo busca o e-mail primario pela API do GitHub.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User user = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = user.getAttribute("email");

        if ("github".equals(registrationId) && (email == null || email.isBlank())) {

            String fetchedEmail = fetchGithubEmail(userRequest.getAccessToken().getTokenValue());

            if (fetchedEmail != null) {

                Map<String, Object> attributes = new HashMap<>(user.getAttributes());

                attributes.put("email", fetchedEmail);

                String userNameAttribute = userRequest.getClientRegistration()
                        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

                // Recria o usuario OAuth2 com os mesmos dados originais, mas agora incluindo o e-mail encontrado.
                return new DefaultOAuth2User(user.getAuthorities(), attributes, userNameAttribute);
            }
        }

        return user;
    }

    // Consulta a API do GitHub para encontrar o e-mail primario e verificado da conta autenticada.
    // Retorna null quando a chamada falha ou quando o usuario nao possui e-mail valido disponivel.
    private String fetchGithubEmail(String accessToken) {

        try {

            HttpHeaders headers = new HttpHeaders();

            headers.setBearerAuth(accessToken);

            headers.set("Accept", "application/vnd.github+json");

            ResponseEntity<String> response = new RestTemplate().exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {

                return null;
            }

            List<GithubEmail> emails = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });

            return emails.stream()

                    .filter(e -> e.primary && e.verified)

                    .map(e -> e.email)

                    .findFirst()

                    .orElse(null);

        } catch (IOException | RestClientException e) {

            return null;
        }
    }

    // Representa cada e-mail retornado pela API do GitHub.
    private static class GithubEmail {

        public String email;

        public boolean primary;

        public boolean verified;
    }
}
