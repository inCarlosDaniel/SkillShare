package Persistencia;

import Modelo.GrupoEstudo;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


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


                Usuario criador = null;


                GrupoEstudo grupo = new GrupoEstudo(idGrupo, nome, materia, exameAlvo, criador, new ArrayList<>());
                
                
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


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, grupo.getNome());


            ps.setString(2, grupo.getMateria());


            ps.setString(3, grupo.getExameAlvo());


            ps.executeUpdate();


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


        String sql = "DELETE FROM grupoestudo WHERE idGrupo = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, id);


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao excluir grupo de estudo: " + e.getMessage());


        }

    }

}
