package com.SkillShare.SkillShare.Teste;

import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.ArrayList;

public class TesteUsuario {

    public static void main(String[] args) throws SQLException {

        // INSERIR
        Usuario usuario = new Usuario(); // cria o objeto de usuário a ser testado

        String emailTeste = "mariana.teste@email.com"; // e-mail único para identificar o usuário durante o teste

        usuario.setNome("Mariana Mendes"); // define o nome do usuário de teste

        usuario.setEmail(emailTeste); // define o e-mail do usuário de teste

        usuario.setSenha("123456"); // define a senha do usuário de teste

        usuario.setHabilidades("Java, Banco de Dados"); // define as habilidades do usuário de teste

        usuario.setInteresses("Programacao Web"); // define os interesses do usuário de teste

        usuario.setDificuldades("Ingles"); // define as dificuldades do usuário de teste

        UsuarioDAO.inserirUsuario(usuario); // persiste o usuário no banco de dados


        // LISTAR
        System.out.println("--------LISTA DE USUARIOS--------"); // exibe cabeçalho da listagem de usuários

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios(); // recupera todos os usuários cadastrados no banco

        for (Usuario u : usuarios) { // percorre a lista de usuários para exibir e localizar o inserido

            if (u.getEmail().equals(emailTeste)) { // identifica o usuário inserido pelo e-mail

                usuario.setIdUsuario(u.getIdUsuario()); // atribui o ID gerado pelo banco ao objeto local

            }

            System.out.println("ID: " + u.getIdUsuario()); // exibe o ID do usuário

            System.out.println("Nome: " + u.getNome()); // exibe o nome do usuário

            System.out.println("Email: " + u.getEmail()); // exibe o e-mail do usuário

            System.out.println("Senha: " + u.getSenha()); // exibe a senha do usuário

            System.out.println("Habilidades: " + u.getHabilidades()); // exibe as habilidades do usuário

            System.out.println("Interesses: " + u.getInteresses()); // exibe os interesses do usuário

            System.out.println("Dificuldades: " + u.getDificuldades()); // exibe as dificuldades do usuário

            System.out.println("-----------------------"); // separador visual entre registros
        }

        if (usuario.getIdUsuario() == 0) { // verifica se o usuário foi encontrado após a inserção

            System.out.println("Usuario inserido nao encontrado para alterar/excluir."); // informa que o ID não foi recuperado

            return; // encerra o teste sem prosseguir para alteração e exclusão

        }


        // ALTERAR
        usuario.setNome("Mariana Gomes"); // atualiza o nome do usuário

        usuario.setEmail("mariana.gomes@email.com"); // atualiza o e-mail do usuário

        usuario.setSenha("654321"); // atualiza a senha do usuário

        usuario.setHabilidades("Java, JSP, MySQL"); // atualiza as habilidades do usuário

        usuario.setInteresses("Desenvolvimento Full Stack"); // atualiza os interesses do usuário

        usuario.setDificuldades("Algoritmos"); // atualiza as dificuldades do usuário

        UsuarioDAO.alterarUsuario(usuario); // persiste as alterações do usuário no banco de dados


        // EXCLUIR
        UsuarioDAO.excluirUsuario(usuario.getIdUsuario()); // remove o usuário do banco de dados pelo seu ID
    }
}
