package com.SkillShare.SkillShare.Persistencia;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.time.LocalDateTime;

import java.util.ArrayList;

import com.SkillShare.SkillShare.Modelo.Anexo;

import com.SkillShare.SkillShare.Modelo.Postagem;

import com.SkillShare.SkillShare.Modelo.Usuario;

// DAO responsavel por postagens, comentarios, upvotes e anexos ligados ao mural.
public class PostagemDAO {







    // Lista postagens principais, ignorando registros que representam comentarios ou upvotes.
    public static ArrayList<Postagem> listarPostagens() throws SQLException {

        ArrayList<Postagem> postagens = new ArrayList<>();

        String sql = "SELECT p.idPostagem, p.conteudo, p.dataPublicacao, p.tipo, p.idReferencia, p.upvoteCount, " +
                     "u.idUsuario, u.nome, u.email, u.senha, u.habilidades, u.interesses, u.dificuldades " +
                     "FROM postagem p LEFT JOIN usuario u ON p.idUsuario = u.idUsuario " +
                     "WHERE p.tipo = 'POST' " +
                     "ORDER BY p.upvoteCount DESC, p.dataPublicacao DESC";

        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) postagens.add(mapear(rs));

        } catch (SQLException e) {

            System.out.println("Erro ao listar postagens: " + e.getMessage());

            throw e;
        }

        for (Postagem p : postagens) p.setAnexos(listarAnexosPorPostagem(p.getidPostagem()));

        return postagens;
    }


    // Consulta postagens por tipo; quando recebe referencia, filtra pelo item original.
    private static ArrayList<Postagem> listarPorTipo(String tipo, Integer idReferencia) throws SQLException {

        ArrayList<Postagem> postagens = new ArrayList<>();

        String sql = "SELECT p.idPostagem, p.conteudo, p.dataPublicacao, p.tipo, p.idReferencia, p.upvoteCount, " +
                     "u.idUsuario, u.nome, u.email, u.senha, u.habilidades, u.interesses, u.dificuldades " +
                     "FROM postagem p LEFT JOIN usuario u ON p.idUsuario = u.idUsuario " +
                     "WHERE p.tipo = ?" +
                     (idReferencia != null ? " AND p.idReferencia = ?" : "") +
                     " ORDER BY p.dataPublicacao DESC";

        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, tipo);

            if (idReferencia != null) ps.setInt(2, idReferencia);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    postagens.add(mapear(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar postagens: " + e.getMessage());

            throw e;
        }

        for (Postagem p : postagens) {

            p.setAnexos(listarAnexosPorPostagem(p.getidPostagem()));
        }

        return postagens;
    }

    // Converte uma linha do ResultSet em objeto Postagem com autor e metadados.
    private static Postagem mapear(ResultSet rs) throws SQLException {

        Usuario autor = new Usuario(
                rs.getInt("idUsuario"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                rs.getString("habilidades"),
                rs.getString("interesses"),
                rs.getString("dificuldades")
        );

        Postagem p = new Postagem(autor, rs.getString("conteudo"),
                rs.getObject("dataPublicacao", LocalDateTime.class),
                rs.getInt("idPostagem"), null);

        p.setTipo(rs.getString("tipo"));

        int ref = rs.getInt("idReferencia");

        if (!rs.wasNull()) p.setIdReferencia(ref);

        p.setUpvoteCount(rs.getInt("upvoteCount"));

        return p;
    }

    // Lista os anexos cadastrados para uma postagem.
    public static ArrayList<Anexo> listarAnexosPorPostagem(int idPostagem) throws SQLException {

        ArrayList<Anexo> anexos = new ArrayList<>();

        String sql = "SELECT * FROM anexo WHERE idPostagem = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPostagem);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                anexos.add(new Anexo(
                        rs.getInt("idAnexo"),
                        rs.getString("nomeArquivo"),
                        rs.getString("tipo"),
                        rs.getString("url")

                ));
            }
        }
        return anexos;
    }


    // Busca uma postagem especifica pelo ID.
    public static Postagem buscarPostagemPorId(int id) throws SQLException {

        String sql = "SELECT p.idPostagem, p.conteudo, p.dataPublicacao, p.tipo, p.idReferencia, p.upvoteCount, " +
                     "u.idUsuario, u.nome, u.email, u.senha, u.habilidades, u.interesses, u.dificuldades " +
                     "FROM postagem p LEFT JOIN usuario u ON p.idUsuario = u.idUsuario " +
                     "WHERE p.idPostagem = ?";

        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) return mapear(rs);
            }

            return null;

        } catch (SQLException e) {

            System.out.println("Erro ao buscar postagem: " + e.getMessage());

            throw e;
        }
    }





    // Insere postagem, comentario ou upvote e guarda o ID gerado.
    public static void inserirPostagem(Postagem postagem) throws SQLException {

        String sql = "INSERT INTO postagem (conteudo, idUsuario, dataPublicacao, tipo, idReferencia) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, postagem.getConteudo());

            ps.setInt(2, postagem.getAutor().getIdUsuario());

            ps.setObject(3, postagem.getDataPublicacao());

            ps.setString(4, postagem.getTipo() != null ? postagem.getTipo() : "POST");

            if (postagem.getIdReferencia() != null) {
                ps.setInt(5, postagem.getIdReferencia());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) postagem.setidPostagem(rs.getInt(1));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao inserir postagem: " + e.getMessage());

            throw e;
        }
    }





    // Atualiza conteudo, autor e data de uma postagem existente.
    public static void alterarPostagem(Postagem postagem) throws SQLException {

        String sql = "UPDATE postagem SET conteudo = ?, idUsuario = ?, dataPublicacao = ? WHERE idPostagem = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, postagem.getConteudo());

            ps.setInt(2, postagem.getAutor().getIdUsuario());

            ps.setObject(3, postagem.getDataPublicacao());

            ps.setInt(4, postagem.getidPostagem());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar postagem: " + e.getMessage());

            throw e;
        }
    }

    // Lista postagens principais feitas por um usuario.
    public static ArrayList<Postagem> listarPostagensPorUsuario(int idUsuario) throws SQLException {

        ArrayList<Postagem> postagens = new ArrayList<>();

        String sql = "SELECT p.idPostagem, p.conteudo, p.dataPublicacao, p.tipo, p.idReferencia, p.upvoteCount, " +
                     "u.idUsuario, u.nome, u.email, u.senha, u.habilidades, u.interesses, u.dificuldades " +
                     "FROM postagem p LEFT JOIN usuario u ON p.idUsuario = u.idUsuario " +
                     "WHERE p.idUsuario = ? AND p.tipo = 'POST' " +
                     "ORDER BY p.upvoteCount DESC, p.dataPublicacao DESC";

        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) postagens.add(mapear(rs));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar postagens do usuario: " + e.getMessage());

            throw e;
        }

        for (Postagem p : postagens) p.setAnexos(listarAnexosPorPostagem(p.getidPostagem()));

        return postagens;
    }

    // Lista postagens do mural/quiz ligadas a um grupo de estudo.
    public static ArrayList<Postagem> listarPostagensPorGrupo(int idGrupo) throws SQLException {

        ArrayList<Postagem> postagens = new ArrayList<>();

        String sql = "SELECT p.idPostagem, p.conteudo, p.dataPublicacao, p.tipo, p.idReferencia, p.upvoteCount, " +
                     "u.idUsuario, u.nome, u.email, u.senha, u.habilidades, u.interesses, u.dificuldades " +
                     "FROM postagem p LEFT JOIN usuario u ON p.idUsuario = u.idUsuario " +
                     "WHERE p.idReferencia = ? AND p.tipo IN ('MURAL', 'QUIZ') " +
                     "ORDER BY p.dataPublicacao DESC";

        try (Connection cn = Conexao.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idGrupo);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) postagens.add(mapear(rs));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar postagens do grupo: " + e.getMessage());

            throw e;
        }

        for (Postagem p : postagens) p.setAnexos(listarAnexosPorPostagem(p.getidPostagem()));

        return postagens;
    }

    // Busca os registros de upvote de uma postagem.
    public static ArrayList<Postagem> buscarUpvotes(int idPostagem) throws SQLException {

        return listarPorTipo("UPVOTE", idPostagem);
    }

    // Busca os comentarios de uma postagem.
    public static ArrayList<Postagem> buscarComentarios(int idPostagem) throws SQLException {

        return listarPorTipo("COMENTARIO", idPostagem);
    }





    // Incrementa o contador de upvotes da postagem original.
    public static void incrementarUpvote(int idPostagem) throws SQLException {

        String sql = "UPDATE postagem SET upvoteCount = upvoteCount + 1 WHERE idPostagem = ?";

        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPostagem);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao incrementar upvote: " + e.getMessage());

            throw e;
        }
    }

    // Decrementa o contador de upvotes sem deixar o valor ficar negativo.
    public static void decrementarUpvote(int idPostagem) throws SQLException {

        String sql = "UPDATE postagem SET upvoteCount = GREATEST(0, upvoteCount - 1) WHERE idPostagem = ?";

        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPostagem);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao decrementar upvote: " + e.getMessage());

            throw e;
        }
    }





    // Remove uma postagem pelo ID.
    public static void excluirPostagem(int id) throws SQLException {

        String sql = "DELETE FROM postagem WHERE idPostagem = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir postagem: " + e.getMessage());

            throw e;
        }
    }

}
