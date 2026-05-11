package Teste;

import Modelo.Postagem;
import Modelo.Usuario;
import Persistencia.PostagemDAO;
import Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestePostagem {

    public static void main(String[] args) throws SQLException {

        Usuario autor = new Usuario();

        String emailAutor = "autor.postagem@email.com";

        autor.setNome("Kauan Silva");

        autor.setEmail(emailAutor);

        autor.setSenha("123456");

        autor.setHabilidades("Java");

        autor.setInteresses("Postagens");

        autor.setDificuldades("Nenhuma");

        UsuarioDAO.inserirUsuario(autor);

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios();

        for (Usuario u : usuarios) {

            if (u.getEmail().equals(emailAutor)) {

                autor.setidUsuario(u.getidUsuario());

                break;

            }

        }

        if (autor.getidUsuario() == 0) {

            System.out.println("Usuario autor nao encontrado para testar postagem.");

            return;

        }


        // INSERIR
        Postagem postagem = new Postagem();

        String conteudoTeste = "Conteudo da postagem de teste";

        postagem.setConteudo(conteudoTeste);

        postagem.setAutor(autor);

        postagem.setDataPublicacao(LocalDateTime.now());

        postagem.setAnexos(new ArrayList<>());

        PostagemDAO.inserirPostagem(postagem);


        // LISTAR
        System.out.println("--------LISTA DE POSTAGENS--------");

        ArrayList<Postagem> postagens = PostagemDAO.listarPostagens();

        for (Postagem p : postagens) {

            if (p.getConteudo().equals(conteudoTeste)) {

                postagem.setidPostagem(p.getidPostagem());

            }

            System.out.println("ID: " + p.getidPostagem());

            System.out.println("Conteudo: " + p.getConteudo());

            if (p.getAutor() != null) {

                System.out.println("Autor: " + p.getAutor().getNome());

            } else {

                System.out.println("Autor: Nao encontrado");

            }

            System.out.println("Data de Publicacao: " + p.getDataPublicacao());

            System.out.println("-----------------------");
        }

        if (postagem.getidPostagem() == 0) {

            System.out.println("Postagem inserida nao encontrada para alterar/excluir.");

            UsuarioDAO.excluirUsuario(autor.getidUsuario());

            return;

        }


        // ALTERAR
        postagem.setConteudo("Conteudo da postagem atualizado.");

        postagem.setDataPublicacao(LocalDateTime.now());

        PostagemDAO.alterarPostagem(postagem);


        // EXCLUIR
        PostagemDAO.excluirPostagem(postagem.getidPostagem());

        UsuarioDAO.excluirUsuario(autor.getidUsuario());
    }
}
