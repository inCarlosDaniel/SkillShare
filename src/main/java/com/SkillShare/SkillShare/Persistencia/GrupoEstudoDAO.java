package com.SkillShare.SkillShare.Persistencia;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;

import java.util.HashSet;

import com.SkillShare.SkillShare.Modelo.GrupoEstudo;

import com.SkillShare.SkillShare.Modelo.Usuario;

// DAO responsavel por grupos de estudo e pelos vinculos entre grupos e membros.
public class GrupoEstudoDAO {

    // Lista todos os grupos, incluindo criador e membros de cada grupo.
    public static ArrayList<GrupoEstudo> listarGruposEstudo() throws SQLException {

        ArrayList<GrupoEstudo> grupos = new ArrayList<>();

        String sql = "SELECT g.*, u.idUsuario AS cId, u.nome AS cNome, u.email AS cEmail, " +
                     "u.habilidades AS cHabilidades, u.interesses AS cInteresses, u.dificuldades AS cDificuldades " +
                     "FROM grupoestudo g LEFT JOIN usuario u ON g.idCriador = u.idUsuario";

        try (Connection cn = Conexao.getConnection();

             Statement st = cn.createStatement();

             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                int idGrupo = rs.getInt("idGrupo");

                ArrayList<Usuario> membros = listarMembrosGrupo(idGrupo);

                Usuario criador = rs.getObject("cId") != null
                        ? new Usuario(rs.getInt("cId"), rs.getString("cNome"), rs.getString("cEmail"),
                                      null, rs.getString("cHabilidades"), rs.getString("cInteresses"), rs.getString("cDificuldades"))
                        : null;

                grupos.add(new GrupoEstudo(
                        idGrupo,
                        rs.getString("nome"),
                        rs.getString("materia"),
                        rs.getString("exameAlvo"),
                        criador,
                        membros
                ));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar grupos de estudo: " + e.getMessage());

            throw e;
        }

        return grupos;
    }

    // Busca um grupo especifico pelo ID, trazendo tambem criador e membros.
    public static GrupoEstudo buscarGrupoEstudoPorId(int id) throws SQLException {

        String sql = "SELECT g.*, u.idUsuario AS cId, u.nome AS cNome, u.email AS cEmail, " +
                     "u.habilidades AS cHabilidades, u.interesses AS cInteresses, u.dificuldades AS cDificuldades " +
                     "FROM grupoestudo g LEFT JOIN usuario u ON g.idCriador = u.idUsuario WHERE g.idGrupo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                ArrayList<Usuario> membros = listarMembrosGrupo(id);

                Usuario criador = rs.getObject("cId") != null
                        ? new Usuario(rs.getInt("cId"), rs.getString("cNome"), rs.getString("cEmail"),
                                      null, rs.getString("cHabilidades"), rs.getString("cInteresses"), rs.getString("cDificuldades"))
                        : null;

                return new GrupoEstudo(rs.getInt("idGrupo"), rs.getString("nome"),
                        rs.getString("materia"), rs.getString("exameAlvo"), criador, membros);
            }

            return null;

        } catch (SQLException e) {

            System.out.println("Erro ao buscar grupo de estudo: " + e.getMessage());

            throw e;
        }
    }

    // Cria um grupo e coloca o criador como membro automaticamente.
    public static void inserirGrupoEstudo(GrupoEstudo grupo) throws SQLException {

        String sql = "INSERT INTO grupoestudo (nome, materia, exameAlvo, idCriador) VALUES (?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, grupo.getNome());

            ps.setString(2, grupo.getMateria());

            ps.setString(3, grupo.getExameAlvo());

            ps.setInt(4, grupo.getCriador().getIdUsuario());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    grupo.setidGrupo(rs.getInt(1));
                }
            }

            if (grupo.getidGrupo() == null || grupo.getidGrupo() == 0) {

                throw new SQLException("Nao foi possivel obter o ID gerado para o grupo '" + grupo.getNome() + "'");
            }

            HashSet<Integer> inseridos = new HashSet<>();

            inserirMembroGrupo(cn, grupo.getidGrupo(), grupo.getCriador(), inseridos);

            if (grupo.getMembros() != null) {

                for (Usuario membro : grupo.getMembros()) {

                    inserirMembroGrupo(cn, grupo.getidGrupo(), membro, inseridos);
                }
            }

        } catch (SQLException e) {

            System.out.println("Erro ao inserir grupo de estudo: " + e.getMessage());

            throw e;
        }
    }

    // Atualiza os dados principais de um grupo ja cadastrado.
    public static void alterarGrupoEstudo(GrupoEstudo grupo) throws SQLException {

        String sql = "UPDATE grupoestudo SET nome = ?, materia = ?, exameAlvo = ? WHERE idGrupo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, grupo.getNome());

            ps.setString(2, grupo.getMateria());

            ps.setString(3, grupo.getExameAlvo());

            ps.setInt(4, grupo.getidGrupo());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar grupo de estudo: " + e.getMessage());

            throw e;
        }
    }

    // Exclui primeiro os membros vinculados e depois remove o grupo.
    public static void excluirGrupoEstudo(int id) throws SQLException {

        String sqlMembros = "DELETE FROM grupo_membro WHERE idGrupo = ?";

        String sql = "DELETE FROM grupoestudo WHERE idGrupo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement psMembros = cn.prepareStatement(sqlMembros);

             PreparedStatement ps = cn.prepareStatement(sql)) {

            psMembros.setInt(1, id);

            psMembros.executeUpdate();

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir grupo de estudo: " + e.getMessage());

            throw e;
        }
    }

    // Busca os usuarios que fazem parte de um grupo.
    private static ArrayList<Usuario> listarMembrosGrupo(int idGrupo) throws SQLException {

        ArrayList<Usuario> membros = new ArrayList<>();

        String sql = "SELECT u.* FROM usuario u INNER JOIN grupo_membro gm ON u.idUsuario = gm.idUsuario WHERE gm.idGrupo = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idGrupo);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                membros.add(new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("habilidades"),
                        rs.getString("interesses"),
                        rs.getString("dificuldades")

                ));
            }
        }

        return membros;
    }


    // Lista apenas os grupos em que o usuario informado participa como membro.
    public static ArrayList<GrupoEstudo> listarGruposPorUsuario(int idUsuario) throws SQLException {

        ArrayList<GrupoEstudo> grupos = new ArrayList<>();

        String sql = "SELECT g.*, u.idUsuario AS cId, u.nome AS cNome, u.email AS cEmail, " +
                     "u.habilidades AS cHabilidades, u.interesses AS cInteresses, u.dificuldades AS cDificuldades " +
                     "FROM grupoestudo g LEFT JOIN usuario u ON g.idCriador = u.idUsuario " +
                     "INNER JOIN grupo_membro gm ON g.idGrupo = gm.idGrupo WHERE gm.idUsuario = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int idGrupo = rs.getInt("idGrupo");

                ArrayList<Usuario> membros = listarMembrosGrupo(idGrupo);

                Usuario criador = rs.getObject("cId") != null
                        ? new Usuario(rs.getInt("cId"), rs.getString("cNome"), rs.getString("cEmail"),
                                      null, rs.getString("cHabilidades"), rs.getString("cInteresses"), rs.getString("cDificuldades"))
                        : null;

                grupos.add(new GrupoEstudo(
                        idGrupo,
                        rs.getString("nome"),
                        rs.getString("materia"),
                        rs.getString("exameAlvo"),
                        criador,
                        membros
                ));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar grupos do usuario: " + e.getMessage());

            throw e;
        }

        return grupos;
    }


    // Busca grupos pelo nome usando comparacao parcial, sem diferenciar maiusculas de minusculas.
    public static ArrayList<GrupoEstudo> buscarGruposPorNome(String nome) throws SQLException {

        ArrayList<GrupoEstudo> grupos = new ArrayList<>();

        String sql = "SELECT g.*, u.idUsuario AS cId, u.nome AS cNome, u.email AS cEmail, " +
                     "u.habilidades AS cHabilidades, u.interesses AS cInteresses, u.dificuldades AS cDificuldades " +
                     "FROM grupoestudo g LEFT JOIN usuario u ON g.idCriador = u.idUsuario " +
                     "WHERE LOWER(g.nome) LIKE LOWER(?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int idGrupo = rs.getInt("idGrupo");

                ArrayList<Usuario> membros = listarMembrosGrupo(idGrupo);

                Usuario criador = rs.getObject("cId") != null
                        ? new Usuario(rs.getInt("cId"), rs.getString("cNome"), rs.getString("cEmail"),
                                      null, rs.getString("cHabilidades"), rs.getString("cInteresses"), rs.getString("cDificuldades"))
                        : null;

                grupos.add(new GrupoEstudo(
                        idGrupo,
                        rs.getString("nome"),
                        rs.getString("materia"),
                        rs.getString("exameAlvo"),
                        criador,
                        membros
                ));
            }

        } catch (SQLException e) {

            System.out.println("Erro ao buscar grupos por nome: " + e.getMessage());

            throw e;
        }

        return grupos;
    }

    // Adiciona um usuario ao grupo somente se ele ainda nao for membro.
    public static void entrarNoGrupo(int idGrupo, int idUsuario) throws SQLException {

        String checkSql = "SELECT COUNT(*) FROM grupo_membro WHERE idGrupo = ? AND idUsuario = ?";

        String insertSql = "INSERT INTO grupo_membro (idGrupo, idUsuario) VALUES (?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement checkPs = cn.prepareStatement(checkSql)) {

            checkPs.setInt(1, idGrupo);

            checkPs.setInt(2, idUsuario);

            ResultSet rsCheck = checkPs.executeQuery();

            if (rsCheck.next() && rsCheck.getInt(1) > 0) return;

            try (PreparedStatement ps = cn.prepareStatement(insertSql)) {

                ps.setInt(1, idGrupo);

                ps.setInt(2, idUsuario);

                ps.executeUpdate();
            }
        } catch (SQLException e) {

            System.out.println("Erro ao entrar no grupo: " + e.getMessage());

            throw e;
        }
    }

    // Insere membro reutilizando a conexao aberta e evitando duplicatas na mesma operacao.
    private static void inserirMembroGrupo(Connection cn, int idGrupo, Usuario usuario,
                                           HashSet<Integer> inseridos) throws SQLException {

        if (idGrupo == 0 || usuario == null || usuario.getIdUsuario() == null
                || usuario.getIdUsuario() == 0 || inseridos.contains(usuario.getIdUsuario())) {

            return;
        }

        String sql = "INSERT INTO grupo_membro (idGrupo, idUsuario) VALUES (?, ?)";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idGrupo);

            ps.setInt(2, usuario.getIdUsuario());

            ps.executeUpdate();

            inseridos.add(usuario.getIdUsuario());
        }
    }
}
