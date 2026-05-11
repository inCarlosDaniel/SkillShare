package Persistencia;

import Modelo.GrupoEstudo;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;


/**
 *
 * @author Carlos Daniel
 */
public class GrupoEstudoDAO {

    //listagem
    public static ArrayList<GrupoEstudo> listarGruposEstudo() throws SQLException {


        ArrayList<GrupoEstudo> grupos = new ArrayList<>();


        try (Connection cn = Conexao.getConnection()) {


            String sql = "SELECT * FROM grupoestudo";


            Statement st = cn.createStatement();


            ResultSet rs = st.executeQuery(sql);


            while (rs.next()) {


                int idGrupo = rs.getInt("idGrupo");


                String nome = rs.getString("nome");


                String materia = rs.getString("materia");


                String exameAlvo = rs.getString("exameAlvo");


                ArrayList<Usuario> membros = listarMembrosGrupo(idGrupo);


                Usuario criador = null;


                if (!membros.isEmpty()) {

                    criador = membros.get(0);

                }


                GrupoEstudo grupo = new GrupoEstudo(idGrupo, nome, materia, exameAlvo, criador, membros);
                
                
                grupos.add(grupo);

            }

        } catch (SQLException e) {


            System.out.println("Erro ao listar grupos de estudo: " + e.getMessage());


        }


        return grupos;


    }

    // inserir
    public static void inserirGrupoEstudo(GrupoEstudo grupo) throws SQLException {


        String sql = "INSERT INTO grupoestudo (nome, materia, exameAlvo) VALUES (?, ?, ?)";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            ps.setString(1, grupo.getNome());


            ps.setString(2, grupo.getMateria());


            ps.setString(3, grupo.getExameAlvo());


            ps.executeUpdate();


            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    grupo.setidGrupo(rs.getInt(1));

                }

            }


            HashSet<Integer> usuariosInseridos = new HashSet<>(); 


            inserirMembroGrupo(cn, grupo.getidGrupo(), grupo.getCriador(), usuariosInseridos);


            if (grupo.getMembros() != null) {

                for (Usuario membro : grupo.getMembros()) {

                    inserirMembroGrupo(cn, grupo.getidGrupo(), membro, usuariosInseridos);

                }

            }


        } catch (SQLException e) {


            System.out.println("Erro ao inserir grupo de estudo: " + e.getMessage());


        }


    }

    //alterar
    public static void alterarGrupoEstudo(GrupoEstudo grupo) throws SQLException {


        String sql = "UPDATE grupoestudo SET nome = ?, materia = ?, exameAlvo = ? WHERE idGrupo = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, grupo.getNome());


            ps.setString(2, grupo.getMateria());


            ps.setString(3, grupo.getExameAlvo());


            ps.setInt(4, grupo.getidGrupo());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao alterar grupo de estudo: " + e.getMessage());


        }

    }

    //excluir
    public static void excluirGrupoEstudo(int id) throws SQLException {


        String sqlMembros = "DELETE FROM grupo_membro WHERE idGrupo = ?";


        String sql = "DELETE FROM grupoestudo WHERE idGrupo = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement psMembros = cn.prepareStatement(sqlMembros); PreparedStatement ps = cn.prepareStatement(sql)) {


            psMembros.setInt(1, id);


            psMembros.executeUpdate();


            ps.setInt(1, id);


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao excluir grupo de estudo: " + e.getMessage());


        }

    }

    private static ArrayList<Usuario> listarMembrosGrupo(int idGrupo) throws SQLException {


        ArrayList<Usuario> membros = new ArrayList<>();


        String sql = "SELECT u.* FROM usuario u INNER JOIN grupo_membro gm ON u.idUsuario = gm.idUsuario WHERE gm.idGrupo = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, idGrupo);


            ResultSet rs = ps.executeQuery();


            while (rs.next()) {

                Usuario usuario = new Usuario(


                        rs.getInt("idUsuario"),


                        rs.getString("nome"),


                        rs.getString("email"),


                        rs.getString("senha"),


                        rs.getString("habilidades"),


                        rs.getString("interesses"),


                        rs.getString("dificuldades")


                );


                membros.add(usuario);

            }

        }


        return membros;

    }

    private static void inserirMembroGrupo(Connection cn, int idGrupo, Usuario usuario, HashSet<Integer> usuariosInseridos) throws SQLException {


        if (idGrupo == 0 || usuario == null || usuario.getidUsuario() == 0 || usuariosInseridos.contains(usuario.getidUsuario())) {

            return;

        }


        String sql = "INSERT INTO grupo_membro (idGrupo, idUsuario) VALUES (?, ?)";


        try (PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, idGrupo);


            ps.setInt(2, usuario.getidUsuario());


            ps.executeUpdate();


            usuariosInseridos.add(usuario.getidUsuario());

        }

    }

}
