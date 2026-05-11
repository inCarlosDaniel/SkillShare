package Persistencia;

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
public class UsuarioDAO {

    //listagem

    public static ArrayList<Usuario> listarUsuarios() throws SQLException {


        ArrayList<Usuario> usuarios = new ArrayList<>();


        try (Connection cn = Conexao.getConnection()) {


            String sql = "SELECT * FROM usuario";


            Statement st = cn.createStatement();


            ResultSet rs = st.executeQuery(sql);


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

                usuarios.add(usuario);

            }

        } catch (SQLException e) {


            System.out.println("Erro ao listar usuarios: " + e.getMessage());


        }


        return usuarios;


    }

    // inserir
    public static void inserirUsuario(Usuario usuario) throws SQLException {


        String sql = "INSERT INTO usuario (nome, email, senha, habilidades, interesses, dificuldades) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, usuario.getNome());


            ps.setString(2, usuario.getEmail());


            ps.setString(3, usuario.getSenha());


            ps.setString(4, usuario.getHabilidades());


            ps.setString(5, usuario.getInteresses());


            ps.setString(6, usuario.getDificuldades());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao inserir usuario: " + e.getMessage());


        }

    }

    //alterar

    public static void alterarUsuario(Usuario usuario) throws SQLException {


        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, habilidades = ?, interesses = ?, dificuldades = ? WHERE idUsuario = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, usuario.getNome());


            ps.setString(2, usuario.getEmail());


            ps.setString(3, usuario.getSenha());


            ps.setString(4, usuario.getHabilidades());


            ps.setString(5, usuario.getInteresses());


            ps.setString(6, usuario.getDificuldades());


            ps.setInt(7, usuario.getidUsuario());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao alterar usuario: " + e.getMessage());


        }

    }

    //excluir

    public static void excluirUsuario(int id) throws SQLException {


        String sql = "DELETE FROM usuario WHERE idUsuario = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, id);


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao excluir usuario: " + e.getMessage());
            

        }

    }

}
