package com.SkillShare.SkillShare.service;

import java.security.SecureRandom;

import java.time.Instant;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import com.SkillShare.SkillShare.Modelo.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificacaoService {


    public enum Resultado { OK, INVALIDO, BLOQUEADO }

    private static final int EXPIRACAO_MINUTOS = 15;

    private static final int MAX_TENTATIVAS = 5;

    private final SecureRandom secureRandom = new SecureRandom();

    private final Map<String, Entry> codigos = new ConcurrentHashMap<>();

    private final Map<String, AtomicInteger> tentativas = new ConcurrentHashMap<>();

    private final Map<String, Usuario> pendentes = new ConcurrentHashMap<>();

    private final EmailService emailService;


    // Gera o codigo de verificacao, salva o cadastro pendente e envia por e-mail
    public void enviar(Usuario usuario) {


        String email = usuario.getEmail();

        AtomicInteger tentativasExistentes = tentativas.get(email);
        if (tentativasExistentes != null && tentativasExistentes.get() >= MAX_TENTATIVAS) {
            return;
        }
        tentativas.remove(email);


        String codigo = String.format("%06d", secureRandom.nextInt(1_000_000));


        Instant expiracao = Instant.now().plusSeconds(EXPIRACAO_MINUTOS * 60L);


        codigos.put(email, new Entry(codigo, expiracao));


        pendentes.put(email, usuario);

        log.warn("==================================================");

        log.warn("CODIGO DE VERIFICACAO para {}: {}", email, codigo);

        log.warn("(Expira em {} minutos)", EXPIRACAO_MINUTOS);

        log.warn("==================================================");


        if (!emailService.isDisponivel()) {

            log.warn("EmailService nao configurado — use o codigo acima no campo de verificacao");

            return;
        }


        emailService.enviarEmail(
                email,
                "Codigo de verificacao SkillShare",
                "Seu codigo e: " + codigo + "\nExpira em " + EXPIRACAO_MINUTOS + " minutos.");


    }



    // Confirma se o codigo informado e valido, expirado ou bloqueado por tentativas
    public Resultado confirmar(String email, String codigo) {

        AtomicInteger falhas = tentativas.computeIfAbsent(email, ignored -> new AtomicInteger(0));

        if (falhas.get() >= MAX_TENTATIVAS) {

            return Resultado.BLOQUEADO;
        }

        Entry entry = codigos.get(email);

        if (entry == null || Instant.now().isAfter(entry.expiracao()) || !entry.codigo().equals(codigo)) {

            falhas.incrementAndGet();

            return Resultado.INVALIDO;
        }

        codigos.remove(email);

        tentativas.remove(email);

        return Resultado.OK;
    }



    // Remove e retorna o usuario que estava aguardando a confirmacao do e-mail
    public Usuario extrairPendente(String email) {

        return pendentes.remove(email);
    }


    
    // Remove codigos expirados e limpa dados pendentes sem codigo ativo
    @Scheduled(fixedRate = 60_000)
    public void limparExpirados() {

        Instant agora = Instant.now();

        codigos.entrySet().removeIf(e -> agora.isAfter(e.getValue().expiracao()));

        tentativas.keySet().removeIf(email -> !codigos.containsKey(email));

        pendentes.keySet().removeIf(email -> !codigos.containsKey(email));
    }

    private record Entry(String codigo, Instant expiracao) {}
}
