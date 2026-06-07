package com.SkillShare.SkillShare.Persistencia;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;

import com.SkillShare.SkillShare.Modelo.Usuario;

// DAO responsavel pelo cadastro, busca e atualizacao dos dados de usuario.
public class UsuarioDAO {

    // Lista todos os usuarios cadastrados.
    public static ArrayList<Usuario> listarUsuarios() throws SQLException {

        ArrayList<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuario";

        try (Connection cn = Conexao.getConnection();

             Statement st = cn.createStatement();

             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                usuarios.add(mapear(rs));

            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar usuarios: " + e.getMessage());

            throw e;

        }

        return usuarios;

    }

    // Busca usuario pelo e-mail ou pelo nome, usado no login/autenticacao.
    public static Usuario buscarPorEmailOuNome(String identificador) throws SQLException {

        String sql = "SELECT * FROM usuario WHERE email = ? OR nome = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificador);

            ps.setString(2, identificador);

            ResultSet rs = ps.executeQuery();

            return rs.next() ? mapear(rs) : null;

        } catch (SQLException e) {

            System.out.println("Erro ao buscar usuario: " + e.getMessage());

            throw e;

        }

    }

    // Busca usuario pelo e-mail exato.
    public static Usuario buscarPorEmail(String email) throws SQLException {

        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            return rs.next() ? mapear(rs) : null;

        } catch (SQLException e) {

            System.out.println("Erro ao buscar usuario por email: " + e.getMessage());

            throw e;

        }
    }

    // Insere um novo usuario no banco.
    public static void inserirUsuario(Usuario usuario) throws SQLException {

        String sql = "INSERT INTO usuario (nome, email, senha, habilidades, interesses, dificuldades) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());

            ps.setString(2, usuario.getEmail());

            ps.setString(3, usuario.getSenha());

            ps.setString(4, usuario.getHabilidades());

            ps.setString(5, usuario.getInteresses());

            ps.setString(6, usuario.getDificuldades());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao inserir usuario: " + e.getMessage());

            throw e;

        }
    }

    // Atualiza todos os dados principais do usuario.
    public static void alterarUsuario(Usuario usuario) throws SQLException {

        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, habilidades = ?, interesses = ?, dificuldades = ? WHERE idUsuario = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());

            ps.setString(2, usuario.getEmail());

            ps.setString(3, usuario.getSenha());

            ps.setString(4, usuario.getHabilidades());

            ps.setString(5, usuario.getInteresses());

            ps.setString(6, usuario.getDificuldades());

            ps.setInt(7, usuario.getIdUsuario());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar usuario: " + e.getMessage());

            throw e;

        }
    }

    // Atualiza somente os campos usados no perfil de aprendizado e no algoritmo de match.
    public static void alterarPerfilAprendizado(int idUsuario, String habilidades, String interesses,

            String dificuldades) throws SQLException {

        String sql = "UPDATE usuario SET habilidades = ?, interesses = ?, dificuldades = ? WHERE idUsuario = ?";


        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, habilidades);

            ps.setString(2, interesses);

            ps.setString(3, dificuldades);

            ps.setInt(4, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar perfil de aprendizado: " + e.getMessage());

            throw e;
        }
    }



    // Troca a senha salva de um usuario.
    public static void alterarSenha(int idUsuario, String novaSenha) throws SQLException {

        String sql = "UPDATE usuario SET senha = ? WHERE idUsuario = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, novaSenha);

            ps.setInt(2, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar senha: " + e.getMessage());

            throw e;
        }
    }


    // Atualiza nome e e-mail sem mexer na senha nem no perfil de aprendizado.
    public static void alterarNomeEmail(int idUsuario, String nome, String email) throws SQLException {

        String sql = "UPDATE usuario SET nome = ?, email = ? WHERE idUsuario = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nome);

            ps.setString(2, email);

            ps.setInt(3, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar nome/email: " + e.getMessage());

            throw e;
        }
    }

    // Exclui um usuario e remove antes os registros dependentes para evitar erro de chave estrangeira.
    public static void excluirUsuario(int id) throws SQLException {

        try (Connection cn = Conexao.getConnection()) {

            cn.setAutoCommit(false);

            try {

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM anexo WHERE idPostagem IN (SELECT idPostagem FROM postagem WHERE idUsuario = ?)")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM postagem WHERE idUsuario = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM mensagem WHERE idRemetente = ? OR idDestinatario = ?")) {
                    ps.setInt(1, id);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM material WHERE idUsuario = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM grupo_membro WHERE idUsuario = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM grupo_membro WHERE idGrupo IN (SELECT idGrupo FROM grupoestudo WHERE idCriador = ?)")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM grupoestudo WHERE idCriador = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM usuario WHERE idUsuario = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                cn.commit();

            } catch (SQLException e) {

                cn.rollback();

                throw e;
            }

        } catch (SQLException e) {

            System.out.println("Erro ao excluir usuario: " + e.getMessage());

            throw e;
        }
    }

    // Converte uma linha do ResultSet em objeto Usuario.
    private static Usuario mapear(ResultSet rs) throws SQLException {

        return new Usuario(
                rs.getInt("idUsuario"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("habilidades"),
                rs.getString("interesses"),
                rs.getString("dificuldades")

        );
    }
}
