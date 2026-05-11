package Persistencia;

import Modelo.Postagem;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Carlos Daniel
 */
public class PostagemDAO {


    //listagem

    public static ArrayList<Postagem> listarPostagens() throws SQLException {


        ArrayList<Postagem> postagens = new ArrayList<>();


        try (Connection cn = Conexao.getConnection()) {


            String sql = "SELECT * FROM postagem";


            Statement st = cn.createStatement();


            ResultSet rs = st.executeQuery(sql);


            while (rs.next()) {


                int idPostagem = rs.getInt("idPostagem");


                String conteudo = rs.getString("conteudo");


                int idUsuario = rs.getInt("idUsuario");


                Usuario autor = null;


                for (Usuario usuario : UsuarioDAO.listarUsuarios()) {


                    if (usuario.getidUsuario() == idUsuario) {

                        autor = usuario;

                        break;

                    }

                }


                LocalDateTime dataPublicacao = rs.getObject("dataPublicacao", LocalDateTime.class);


                Postagem postagem = new Postagem(autor, conteudo, dataPublicacao, idPostagem, null);
                

               
                postagens.add(postagem);

            }

        } catch (SQLException e) {


            System.out.println("Erro ao listar postagens: " + e.getMessage());


        }


        return postagens;


    }

    // inserir
    public static void inserirPostagem(Postagem postagem) throws SQLException {


        String sql = "INSERT INTO postagem (conteudo, idUsuario, dataPublicacao) VALUES (?, ?, ?)";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, postagem.getConteudo());


            ps.setInt(2, postagem.getAutor().getidUsuario());


            ps.setObject(3, postagem.getDataPublicacao());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao inserir postagem: " + e.getMessage());


        }

    }

    //alterar

    public static void alterarPostagem(Postagem postagem) throws SQLException {


        String sql = "UPDATE postagem SET conteudo = ?, idUsuario = ?, dataPublicacao = ? WHERE idPostagem = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, postagem.getConteudo());


            ps.setInt(2, postagem.getAutor().getidUsuario());


            ps.setObject(3, postagem.getDataPublicacao());


            ps.setInt(4, postagem.getidPostagem());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao alterar postagem: " + e.getMessage());


        }

    }

    //excluir

    public static void excluirPostagem(int id) throws SQLException {


        String sql = "DELETE FROM postagem WHERE idPostagem = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, id);


            ps.executeUpdate();
            

        } catch (SQLException e) {


            System.out.println("Erro ao excluir postagem: " + e.getMessage());
            

        }

    }

}
