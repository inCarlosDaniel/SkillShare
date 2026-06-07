package com.SkillShare.SkillShare.Persistencia;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;

import com.SkillShare.SkillShare.Modelo.Anexo;

import com.SkillShare.SkillShare.Modelo.Postagem;

// DAO responsavel pelas operacoes de banco da tabela anexo.
public class AnexoDAO {

    // Lista todos os anexos e associa cada um a sua postagem correspondente.
    public static ArrayList<Anexo> listarAnexos() throws SQLException {

        ArrayList<Anexo> anexos = new ArrayList<>();

        String sql = "SELECT * FROM anexo";

        try (Connection cn = Conexao.getConnection();

             Statement st = cn.createStatement();

             ResultSet rs = st.executeQuery(sql)) {

            ArrayList<Postagem> postagens = PostagemDAO.listarPostagens();

            while (rs.next()) {

                int idPostagem = rs.getInt("idPostagem");

                Postagem postagem = postagens.stream()

                        .filter(p -> p.getidPostagem() == idPostagem)

                        .findFirst().orElse(null);

                Anexo anexo = new Anexo(
                        rs.getInt("idAnexo"),
                        rs.getString("nomeArquivo"),
                        rs.getString("tipo"),
                        rs.getString("url")

                );

                anexo.setPostagem(postagem);

                anexos.add(anexo);
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar anexos: " + e.getMessage());

            throw e;
        }

        return anexos;
    }

    // Insere um novo anexo ligado a uma postagem.
    public static void inserirAnexo(Anexo anexo) throws SQLException {

        String sql = "INSERT INTO anexo (nomeArquivo, tipo, url, idPostagem) VALUES (?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, anexo.getNomeArquivo());

            ps.setString(2, anexo.getTipo());

            ps.setString(3, anexo.getUrl());

            ps.setInt(4, anexo.getPostagem().getidPostagem());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao inserir anexo: " + e.getMessage());

            throw e;
        }
    }

    // Atualiza nome, tipo, URL e postagem vinculada de um anexo existente.
    public static void alterarAnexo(Anexo anexo) throws SQLException {

        String sql = "UPDATE anexo SET nomeArquivo = ?, tipo = ?, url = ?, idPostagem = ? WHERE idAnexo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, anexo.getNomeArquivo());

            ps.setString(2, anexo.getTipo());

            ps.setString(3, anexo.getUrl());

            ps.setInt(4, anexo.getPostagem().getidPostagem());

            ps.setInt(5, anexo.getidAnexo());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar anexo: " + e.getMessage());

            throw e;
        }
    }

    // Remove um anexo pelo ID.
    public static void excluirAnexo(int id) throws SQLException {

        String sql = "DELETE FROM anexo WHERE idAnexo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir anexo: " + e.getMessage());

            throw e;
        }
    }
}
