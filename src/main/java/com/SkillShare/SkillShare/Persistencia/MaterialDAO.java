package com.SkillShare.SkillShare.Persistencia;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;

import com.SkillShare.SkillShare.Modelo.Material;

import com.SkillShare.SkillShare.Modelo.Usuario;

// DAO responsavel por materiais, arquivos enviados e imagens de perfil/grupo.
public class MaterialDAO {

    // Lista materiais gerais do repositorio, ignorando registros usados como fotos.
    public static ArrayList<Material> listarMateriais() throws SQLException {

        ArrayList<Material> materiais = new ArrayList<>();

        String sql = "SELECT * FROM material WHERE categoria IS NULL OR (categoria NOT LIKE 'foto-%' AND categoria NOT LIKE 'grupo-%')";

        try (Connection cn = Conexao.getConnection();

             Statement st = cn.createStatement();

             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                int idUsuario = rs.getInt("idUsuario");

                Usuario autor = UsuarioDAO.listarUsuarios().stream()

                        .filter(u -> u.getIdUsuario() == idUsuario)

                        .findFirst().orElse(null);

                materiais.add(new Material(
                        rs.getBlob("arquivo"),
                        autor,
                        rs.getString("categoria"),
                        rs.getInt("idMaterial"),
                        rs.getString("titulo")

                ));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar materiais: " + e.getMessage());

            throw e;
        }

        return materiais;
    }

    // Insere um material completo e atualiza o objeto com o ID gerado pelo banco.
    public static void inserirMaterial(Material material) throws SQLException {

        String sql = "INSERT INTO material (titulo, categoria, arquivo, idUsuario) VALUES (?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, material.getTitulo());

            ps.setString(2, material.getCategoria());

            ps.setBinaryStream(3, material.getArquivo().getBinaryStream(), material.getArquivo().length());

            ps.setInt(4, material.getAutor().getIdUsuario());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    material.setidMaterial(rs.getInt(1));
                }
            }

        } catch (SQLException e) {

            System.out.println("Erro ao inserir material: " + e.getMessage());

            throw e;
        }
    }

    // Lista anexos vinculados a postagens de um grupo, montando-os como materiais para a tela.
    public static ArrayList<Material> listarMateriaisPorGrupo(int idGrupo) throws SQLException {

        ArrayList<Material> materiais = new ArrayList<>();

        String sql = "SELECT a.idAnexo, a.nomeArquivo, a.tipo, a.url, " +
                     "u.idUsuario, u.nome, u.email, u.senha, u.habilidades, u.interesses, u.dificuldades " +
                     "FROM anexo a " +
                     "JOIN postagem p ON a.idPostagem = p.idPostagem " +
                     "JOIN usuario u ON p.idUsuario = u.idUsuario " +
                     "WHERE a.tipo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "grupo-" + idGrupo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Usuario autor = new Usuario(
                        rs.getInt("idUsuario"), rs.getString("nome"), rs.getString("email"),
                        rs.getString("senha"), rs.getString("habilidades"),
                        rs.getString("interesses"), rs.getString("dificuldades")

                );

                Material m = new Material(null, autor, null,
                        rs.getInt("idAnexo"), rs.getString("nomeArquivo"));

                m.setUrl(rs.getString("url"));

                materiais.add(m);
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar materiais do grupo: " + e.getMessage());

            throw e;
        }
        return materiais;
    }

    // Atualiza titulo, categoria, arquivo e autor de um material existente.
    public static void alterarMaterial(Material material) throws SQLException {

        String sql = "UPDATE material SET titulo = ?, categoria = ?, arquivo = ?, idUsuario = ? WHERE idMaterial = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, material.getTitulo());

            ps.setString(2, material.getCategoria());

            ps.setBinaryStream(3, material.getArquivo().getBinaryStream(), material.getArquivo().length());

            ps.setInt(4, material.getAutor().getIdUsuario());

            ps.setInt(5, material.getidMaterial());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar material: " + e.getMessage());

            throw e;
        }
    }

    // Busca a foto de perfil salva para um usuario.
    public static Material buscarFotoPerfilPorUsuario(int idUsuario) throws SQLException {

        String sql = "SELECT * FROM material WHERE idUsuario = ? AND categoria = 'foto-perfil' LIMIT 1";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Usuario autor = new Usuario();

            autor.setIdUsuario(idUsuario);

            return new Material(
                    rs.getBlob("arquivo"),
                    autor,
                    rs.getString("categoria"),
                    rs.getInt("idMaterial"),
                    rs.getString("titulo")

            );

        } catch (SQLException e) {

            System.out.println("Erro ao buscar foto de perfil: " + e.getMessage());

            throw e;
        }
    }

    // Salva a foto/capa de um grupo usando categoria no formato foto-grupo-{idGrupo}.
    public static void inserirFotoGrupo(int idGrupo, int idCriador, String mimeType, byte[] bytes) throws SQLException {

        String categoria = "foto-grupo-" + idGrupo;

        String sqlUpdate = "UPDATE material SET titulo = ?, arquivo = ?, idUsuario = ? WHERE categoria = ?";

        String sqlInsert = "INSERT INTO material (titulo, categoria, arquivo, idUsuario) VALUES (?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement psUpdate = cn.prepareStatement(sqlUpdate)) {

            psUpdate.setString(1, mimeType != null ? mimeType : "image/jpeg");

            psUpdate.setBytes(2, bytes);

            psUpdate.setInt(3, idCriador);

            psUpdate.setString(4, categoria);

            int linhasAlteradas = psUpdate.executeUpdate();

            if (linhasAlteradas > 0) return;

            try (PreparedStatement psInsert = cn.prepareStatement(sqlInsert)) {

                psInsert.setString(1, mimeType != null ? mimeType : "image/jpeg");

                psInsert.setString(2, categoria);

                psInsert.setBytes(3, bytes);

                psInsert.setInt(4, idCriador);

                psInsert.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println("Erro ao inserir foto do grupo: " + e.getMessage());

            throw e;
        }
    }

    // Busca a foto/capa de um grupo pela categoria foto-grupo-{idGrupo}.
    public static Material buscarFotoGrupoPorGrupo(int idGrupo) throws SQLException {

        String sql = "SELECT * FROM material WHERE categoria = ? ORDER BY idMaterial DESC LIMIT 1";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "foto-grupo-" + idGrupo);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return new Material(
                    rs.getBlob("arquivo"),
                    null,
                    rs.getString("categoria"),
                    rs.getInt("idMaterial"),
                    rs.getString("titulo")

            );

        } catch (SQLException e) {

            System.out.println("Erro ao buscar foto do grupo: " + e.getMessage());

            throw e;
        }
    }

    // Insere um arquivo enviado pelo usuario e retorna o ID gerado.
    public static int inserirArquivo(int idUsuario, String titulo, String categoria, byte[] bytes) throws SQLException {

        String sql = "INSERT INTO material (titulo, categoria, arquivo, idUsuario) VALUES (?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titulo);

            ps.setString(2, categoria);

            ps.setBytes(3, bytes);

            ps.setInt(4, idUsuario);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {

            System.out.println("Erro ao inserir arquivo: " + e.getMessage());

            throw e;
        }
        return -1;
    }

    // Salva uma nova foto de perfil para o usuario.
    public static void inserirFotoPerfil(int idUsuario, String mimeType, byte[] bytes) throws SQLException {

        String sql = "INSERT INTO material (titulo, categoria, arquivo, idUsuario) VALUES (?, 'foto-perfil', ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, mimeType != null ? mimeType : "image/jpeg");

            ps.setBytes(2, bytes);

            ps.setInt(3, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao inserir foto de perfil: " + e.getMessage());

            throw e;
        }
    }

    // Remove a foto de perfil atual do usuario.
    public static void excluirFotoPerfilPorUsuario(int idUsuario) throws SQLException {

        String sql = "DELETE FROM material WHERE idUsuario = ? AND categoria = 'foto-perfil'";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir foto de perfil: " + e.getMessage());

            throw e;
        }
    }

    // Exclui um material pelo ID.
    public static void excluirMaterial(int id) throws SQLException {

        String sql = "DELETE FROM material WHERE idMaterial = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir material: " + e.getMessage());

            throw e;
        }
    }
}
