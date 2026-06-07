package com.SkillShare.SkillShare.Teste;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author Carlos Daniel
 */
@SpringBootTest // carrega o contexto completo do Spring Boot para o teste de integração
public class TesteConexao {

    @Autowired // injeta automaticamente o DataSource configurado pelo Spring Boot
    private DataSource dataSource;

    @Test // marca o método como um caso de teste JUnit 5
    void deveEstabelecerConexaoComBancoConfiguradoPeloSpringBoot() throws SQLException {

        System.out.println("Tentando estabelecer conexao com o MySQL pelo Spring Boot..."); // log informativo antes de iniciar a verificação

        assertNotNull(dataSource); // verifica que o DataSource foi injetado corretamente pelo Spring

        try (Connection conexao = dataSource.getConnection()) { // abre uma conexão com o banco usando o DataSource injetado

            assertNotNull(conexao); // verifica que a conexão retornada não é nula

            assertFalse(conexao.isClosed()); // verifica que a conexão está aberta e pronta para uso

            System.out.println("Conexao com MySQL estabelecida com sucesso pelo Spring Boot!"); // confirma sucesso ao obter a conexão

        }
    }
}
