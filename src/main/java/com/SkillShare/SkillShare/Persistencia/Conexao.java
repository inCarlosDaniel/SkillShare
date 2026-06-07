package com.SkillShare.SkillShare.Persistencia;

import java.io.IOException;

import java.io.InputStream;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

import java.util.Properties;

// Centraliza a abertura de conexoes JDBC usando as configuracoes do application.properties.
public class Conexao {

    private static final Properties PROPRIEDADES = carregarPropriedades();

    private static final String DRIVER = obterPropriedade("spring.datasource.driver-class-name", "org.mariadb.jdbc.Driver");

    private static final String URL = obterPropriedade(
            "spring.datasource.url",
            "jdbc:mariadb://localhost:3306/skillshare"
    );

    private static final String USUARIO = obterPropriedade("spring.datasource.username", "root");

    private static final String SENHA = obterPropriedade("spring.datasource.password", "");

    // Abre uma conexao nova com o banco usando driver, URL, usuario e senha configurados.
    public static Connection getConnection() throws SQLException {

        try {

            Class.forName(DRIVER);

            return DriverManager.getConnection(URL, USUARIO, SENHA);

        } catch (ClassNotFoundException e) {

            throw new SQLException("Driver do banco de dados nao foi encontrado: " + DRIVER, e);

        }

    }

    // Carrega o application.properties para reaproveitar as mesmas configuracoes do Spring.
    private static Properties carregarPropriedades() {

        Properties propriedades = new Properties();

        try (InputStream input = Conexao.class.getClassLoader().getResourceAsStream("application.properties")) {

            if (input != null) {

                propriedades.load(input);

            }

        } catch (IOException e) {

            System.out.println("Nao foi possivel ler o application.properties: " + e.getMessage());

        }

        return propriedades;

    }

    // Busca uma propriedade e aceita valores no formato ${VARIAVEL_DE_AMBIENTE}.
    private static String obterPropriedade(String chave, String valorPadrao) {

        String valor = PROPRIEDADES.getProperty(chave);

        if (valor == null || valor.isBlank()) {

            return valorPadrao;
        }


        if (valor.startsWith("${") && valor.endsWith("}")) {

            String nomeVar = valor.substring(2, valor.length() - 1);

            String valorEnv = System.getenv(nomeVar);

            return (valorEnv != null) ? valorEnv : valorPadrao;
        }

        return valor;

    }

}
