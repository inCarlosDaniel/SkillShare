package com.SkillShare.SkillShare.service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PresencaService {

    // Guarda os e-mails dos usuarios que estao conectados ao WebSocket no momento.
    private final Set<String> online = ConcurrentHashMap.newKeySet();

    // Para cada usuario observado, guarda quem quer receber atualizacoes de presenca dele.
    private final Map<String, Set<String>> watchers = new ConcurrentHashMap<>();

    // Marca o usuario como online quando ele abre/conecta no chat em tempo real.
    public void registrarOnline(String email) {

        online.add(email);
    }



    // Remove o usuario da lista de online quando ele desconecta.
    public void registrarOffline(String email) {

        online.remove(email);

        watchers.remove(email);
    }



    // Consulta se um e-mail esta marcado como online.
    public boolean estaOnline(String email) {

        return email != null && online.contains(email);
    }



    // Registra que um usuario quer acompanhar o status online/offline de outro.
    public void adicionarWatcher(String emailObservado, String emailWatcher) {

        watchers.computeIfAbsent(emailObservado, k -> ConcurrentHashMap.newKeySet())

                .add(emailWatcher);
    }



    // Retorna quem deve ser avisado quando o status de presenca desse usuario mudar.
    public Set<String> getWatchers(String email) {

        return watchers.getOrDefault(email, Collections.emptySet());
    }

    

}
