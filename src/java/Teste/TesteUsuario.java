package Teste;

import Modelo.Usuario;
import Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.ArrayList;

public class TesteUsuario {

    public static void main(String[] args) throws SQLException {

        // INSERIR
        Usuario usuario = new Usuario();

        String emailTeste = "mariana.teste@email.com";

        usuario.setNome("Mariana Mendes");

        usuario.setEmail(emailTeste);

        usuario.setSenha("123456");

        usuario.setHabilidades("Java, Banco de Dados");

        usuario.setInteresses("Programacao Web");

        usuario.setDificuldades("Ingles");

        UsuarioDAO.inserirUsuario(usuario);


        // LISTAR
        System.out.println("--------LISTA DE USUARIOS--------");

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios();

        for (Usuario u : usuarios) {

            if (u.getEmail().equals(emailTeste)) {

                usuario.setidUsuario(u.getidUsuario());

            }

            System.out.println("ID: " + u.getidUsuario());

            System.out.println("Nome: " + u.getNome());

            System.out.println("Email: " + u.getEmail());

            System.out.println("Senha: " + u.getSenha());

            System.out.println("Habilidades: " + u.getHabilidades());

            System.out.println("Interesses: " + u.getInteresses());

            System.out.println("Dificuldades: " + u.getDificuldades());

            System.out.println("-----------------------");
        }

        if (usuario.getidUsuario() == 0) {

            System.out.println("Usuario inserido nao encontrado para alterar/excluir.");

            return;

        }


        // ALTERAR
        usuario.setNome("Mariana Gomes");

        usuario.setEmail("mariana.gomes@email.com");

        usuario.setSenha("654321");

        usuario.setHabilidades("Java, JSP, MySQL");

        usuario.setInteresses("Desenvolvimento Full Stack");

        usuario.setDificuldades("Algoritmos");

        UsuarioDAO.alterarUsuario(usuario);


        // EXCLUIR
        UsuarioDAO.excluirUsuario(usuario.getidUsuario());
    }
}
