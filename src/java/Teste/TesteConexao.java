
package Teste;

import Persistencia.Conexao;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Carlos Daniel
 */
public class TesteConexao {

    public static void main(String[] args) {


        System.out.println("Tentando estabelecer conexão com o banco de dados...");


        try {

            Connection conexao = Conexao.getConnection();


        } catch (SQLException e) {


            System.out.println("Falha ao estabelecer conexão com o banco de dados!");


            System.out.println("Erro ao testar a conexão: " + e.getMessage());


        }

        

    }
    
}
