package com.SkillShare.SkillShare.config; 

import jakarta.annotation.PostConstruct; 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Component; 

import com.SkillShare.SkillShare.Persistencia.MensagemDAO; // Importa o DAO de mensagens para configurar a chave de criptografia

@Component // Registra esta classe como um bean componente gerenciado pelo Spring
public class CriptografiaConfig {

    @Value("${app.mensagem.chave-aes}") // Injeta a chave AES definida no arquivo de propriedades 

    private String chaveAes; // Campo que armazena a chave AES usada para criptografar e descriptografar mensagens

    @PostConstruct // Indica que este método deve ser executado logo após o Spring injetar todas as dependências
    public void init() { // Método de inicialização que configura a criptografia das mensagens ao subir a aplicação

        MensagemDAO.setChaveAes(chaveAes); // Repassa a chave AES ao DAO para que ele use na criptografia das mensagens

        try { 

            MensagemDAO.migrarCriptografia(); 

        } catch (Exception e) {

            

            org.slf4j.LoggerFactory.getLogger(CriptografiaConfig.class)

                    .warn("Migração de criptografia falhou: {}", e.getMessage()); 

        }
    }
}
