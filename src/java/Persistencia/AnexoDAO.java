
package Persistencia;

import Modelo.Anexo;
import Modelo.Postagem;
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
public class AnexoDAO {

    //listagem

    public static ArrayList<Anexo> listarAnexos() {


        ArrayList<Anexo> anexos = new ArrayList<>();


        try (Connection cn = Conexao.getConnection()) {


            String sql = "SELECT * FROM anexo";


            Statement st = cn.createStatement();


            ResultSet rs = st.executeQuery(sql);


            while (rs.next()) {

                Anexo anexo = new Anexo();

                anexo.setidAnexo(rs.getInt("idAnexo"));

                anexo.setNomeArquivo(rs.getString("nomeArquivo"));

                anexo.setTipo(rs.getString("tipo"));

                anexo.setUrl(rs.getString("url"));

                int idPostagem = rs.getInt("idPostagem");

                Postagem postagem = null;

                for (Postagem p : PostagemDAO.listarPostagens()) {

                    if (p.getidPostagem() == idPostagem) {

                        postagem = p;

                        break;

                    }

                }

                anexo.setPostagem(postagem);

                anexos.add(anexo);

            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar anexos: " + e.getMessage());

        }


        return anexos;


    }

    // inserir

    public static void inserirAnexo(Anexo anexo) {


        String sql = "INSERT INTO anexo (nomeArquivo, tipo, url, idPostagem) VALUES (?, ?, ?, ?)";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, anexo.getNomeArquivo());

            ps.setString(2, anexo.getTipo());

            ps.setString(3, anexo.getUrl());

            ps.setInt(4, anexo.getPostagem().getidPostagem());

            ps.executeUpdate();

            System.out.println("Anexo inserido com sucesso!");

        } catch (SQLException e) {

            System.out.println("Erro ao inserir anexo: " + e.getMessage());

        }

    }


    //alterar

    public static void alterarAnexo(Anexo anexo) {


        String sql = "UPDATE anexo SET nomeArquivo = ?, tipo = ?, url = ?, idPostagem = ? WHERE idAnexo = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, anexo.getNomeArquivo());

            ps.setString(2, anexo.getTipo());

            ps.setString(3, anexo.getUrl());

            ps.setInt(4, anexo.getPostagem().getidPostagem());

            ps.setInt(5, anexo.getidAnexo());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar anexo: " + e.getMessage());

        }

    }


    //excluir
    public static void excluirAnexo(int id) {


        String sql = "DELETE FROM anexo WHERE idAnexo = ?";


        try (Connection cn = Conexao.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir anexo: " + e.getMessage());

        }

    }




    
}
