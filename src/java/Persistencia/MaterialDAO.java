package Persistencia;

import Modelo.Material;
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
public class MaterialDAO {

    //listagem

    public static ArrayList<Material> listarMateriais() throws SQLException {


        ArrayList<Material> materiais = new ArrayList<>();


        try (Connection cn = Conexao.getConnection()) {


            String sql = "SELECT * FROM material";


            Statement st = cn.createStatement();


            ResultSet rs = st.executeQuery(sql);


            while (rs.next()) {


                int idMaterial = rs.getInt("idMaterial");


                String titulo = rs.getString("titulo");


                String categoria = rs.getString("categoria");


                java.sql.Blob arquivo = rs.getBlob("arquivo");


                int idUsuario = rs.getInt("idUsuario");


                Usuario autor = null;

                for (Usuario usuario : UsuarioDAO.listarUsuarios()) {

                    if (usuario.getidUsuario() == idUsuario) {

                        autor = usuario;

                        break;

                    }

                }

                Material material = new Material(arquivo, autor, categoria, idMaterial, titulo);
                materiais.add(material);

            }


        } catch (SQLException e) {

            System.out.println("Erro ao listar materiais: " + e.getMessage());

        }


        return materiais;

    }


    // inserir

    public static void inserirMaterial(Material material) throws SQLException {

        String sql = "INSERT INTO material (titulo, categoria, arquivo, idUsuario) VALUES (?, ?, ?, ?)";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, material.getTitulo());


            ps.setString(2, material.getCategoria());


            ps.setBlob(3, material.getArquivo());


            ps.setInt(4, material.getAutor().getidUsuario());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao inserir material: " + e.getMessage());


        }

    }


    //alterar

    public static void alterarMaterial(Material material) throws SQLException {


        String sql = "UPDATE material SET titulo = ?, categoria = ?, arquivo = ?, idUsuario = ? WHERE idMaterial = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, material.getTitulo());


            ps.setString(2, material.getCategoria());


            ps.setBlob(3, material.getArquivo());


            ps.setInt(4, material.getAutor().getidUsuario());


            ps.setInt(5, material.getidMaterial());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao alterar material: " + e.getMessage());


        }

    }

    //excluir

    public static void excluirMaterial(int id) throws SQLException {


        String sql = "DELETE FROM material WHERE idMaterial = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, id);


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao excluir material: " + e.getMessage());

            
        }

    }

}
