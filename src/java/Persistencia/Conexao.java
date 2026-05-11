package Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Carlos Daniel
 */
public class Conexao {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String URL = "jdbc:mysql://localhost:3306/skillshare";

    private static final String USUARIO = "root";

    private static final String SENHA = "";

    public static Connection getConnection() throws SQLException {

        try {

            Class.forName(DRIVER);

            System.out.println("Driver foi carregado com sucesso!");

            Connection cn = DriverManager.getConnection(URL, USUARIO, SENHA);

            System.out.println("Conexao estabelecida com sucesso!");

            return cn;

        } catch (ClassNotFoundException e) {

            System.out.println("Driver do MySQL nao foi encontrado!");

        } catch (SQLException e) {

            System.out.println("Erro ao estabelecer a conexao: " + e.getMessage());

        }

        return null;

    }

}
