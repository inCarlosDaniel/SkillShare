package com.SkillShare.SkillShare.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    // Usa ObjectProvider para permitir que a aplicacao rode mesmo sem SMTP configurado.
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    // Remetente usado nos e-mails; 
    @Value("${spring.mail.from:${spring.mail.username:}}")
    private String from;

    // Verifica se existe configuracao real de e-mail no Spring.
    // Se retornar false, o sistema nao envia e-mail e usa o codigo mostrado no log.
    public boolean isDisponivel() {

        return mailSenderProvider.getIfAvailable() != null;
    }

    
    // Envia e-mail em segundo plano usando o taskExecutor, para nao travar a requisicao do usuario.
    @Async("taskExecutor")
    public void enviarEmail(String destinatario, String assunto, String texto) {

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {

            log.error("JavaMailSender nao configurado — verifique MAIL_USERNAME e MAIL_PASSWORD");

            return;
        }

        try {

            SimpleMailMessage mensagem = new SimpleMailMessage();

            if (!from.isBlank()) {

                mensagem.setFrom(sanitizar(from));
            }

            mensagem.setTo(sanitizar(destinatario));

            mensagem.setSubject(sanitizar(assunto));

            mensagem.setText(texto);

            mailSender.send(mensagem);

            log.info("Email enviado com sucesso para: {}", destinatario);

        } catch (MailException e) {

            log.error("Falha ao enviar email para '{}': {} — verifique as credenciais SMTP e a senha de app do Gmail",
                    destinatario, e.getMessage());
        } catch (Exception e) {

            log.error("Erro inesperado ao enviar email para '{}': {}", destinatario, e.getMessage());
        }
    }



    // Remove quebras de linha para evitar injecao de cabecalho no e-mail.
    private String sanitizar(String valor) {

        if (valor == null) return "";

        return valor.replaceAll("[\r\n]", "");
    }



}
