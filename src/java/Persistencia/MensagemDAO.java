package Persistencia;

import Modelo.Mensagem;
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
public class MensagemDAO {


    //listagem

    public static ArrayList<Mensagem> listarMensagens() throws SQLException {


        ArrayList<Mensagem> mensagens = new ArrayList<>();


        try (Connection cn = Conexao.getConnection()) {


            String sql = "SELECT * FROM mensagem";


            Statement st = cn.createStatement();


            ResultSet rs = st.executeQuery(sql);


            while (rs.next()) {


                int idMensagem = rs.getInt("idMensagem");


                String texto = rs.getString("texto");


                LocalDateTime dataEnvio = rs.getObject("dataEnvio", LocalDateTime.class);


                boolean lida = rs.getBoolean("lida");



                int idRemetente = rs.getInt("idRemetente");


                Usuario remetente = null;



                for (Usuario usuario : UsuarioDAO.listarUsuarios()) {


                    if (usuario.getidUsuario() == idRemetente) {


                        remetente = usuario;

                        break;

                    }

                }

                int idDestinatario = rs.getInt("idDestinatario");


                Usuario destinatario = null;

                for (Usuario usuario : UsuarioDAO.listarUsuarios()) {


                    if (usuario.getidUsuario() == idDestinatario) {

                        destinatario = usuario;

                        break;

                    }

                }

                Mensagem mensagem = new Mensagem(dataEnvio, destinatario, idMensagem, lida, remetente, texto);

                mensagens.add(mensagem);

            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar mensagens: " + e.getMessage());

        }

        return mensagens;

    }

    // inserir

    public static void inserirMensagem(Mensagem mensagem) throws SQLException {


        String sql = "INSERT INTO mensagem (texto, dataEnvio, lida, idRemetente, idDestinatario) VALUES (?, ?, ?, ?, ?)";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, mensagem.getTexto());


            ps.setObject(2, mensagem.getDataEnvio());


            ps.setBoolean(3, mensagem.isLida());


            ps.setInt(4, mensagem.getRemetente().getidUsuario());


            ps.setInt(5, mensagem.getDestinatario().getidUsuario());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao inserir mensagem: " + e.getMessage());


        }

    }

    //alterar

    public static void alterarMensagem(Mensagem mensagem) throws SQLException {


        String sql = "UPDATE mensagem SET texto = ?, dataEnvio = ?, lida = ?, idRemetente = ?, idDestinatario = ? WHERE idMensagem = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setString(1, mensagem.getTexto());


            ps.setObject(2, mensagem.getDataEnvio());


            ps.setBoolean(3, mensagem.isLida());


            ps.setInt(4, mensagem.getRemetente().getidUsuario());


            ps.setInt(5, mensagem.getDestinatario().getidUsuario());


            ps.setInt(6, mensagem.getidMensagem());


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao alterar mensagem: " + e.getMessage());


        }

    }

    //excluir

    public static void excluirMensagem(int id) throws SQLException {


        String sql = "DELETE FROM mensagem WHERE idMensagem = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {


            ps.setInt(1, id);


            ps.executeUpdate();


        } catch (SQLException e) {


            System.out.println("Erro ao excluir mensagem: " + e.getMessage());
            

        }

    }

}
