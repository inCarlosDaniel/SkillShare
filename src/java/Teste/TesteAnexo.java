package Teste;

import Modelo.Anexo;
import Modelo.Postagem;
import Modelo.Usuario;
import Persistencia.AnexoDAO;
import Persistencia.PostagemDAO;
import Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TesteAnexo {

    public static void main(String[] args) throws SQLException {

        Usuario autor = new Usuario();

        String emailAutor = "autor.anexo@email.com";

        autor.setNome("Autor Anexo");

        autor.setEmail(emailAutor);

        autor.setSenha("123456");

        autor.setHabilidades("Java");

        autor.setInteresses("Anexos");

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

            System.out.println("Usuario autor nao encontrado para testar anexo.");

            return;

        }

        Postagem postagem = new Postagem();

        String conteudoPostagem = "Postagem para anexo";

        postagem.setConteudo(conteudoPostagem);

        postagem.setAutor(autor);

        postagem.setDataPublicacao(LocalDateTime.now());

        postagem.setAnexos(new ArrayList<>());

        PostagemDAO.inserirPostagem(postagem);

        ArrayList<Postagem> postagens = PostagemDAO.listarPostagens();

        for (Postagem p : postagens) {

            if (p.getConteudo().equals(conteudoPostagem)) {

                postagem.setidPostagem(p.getidPostagem());

                break;

            }

        }

        if (postagem.getidPostagem() == 0) {

            System.out.println("Postagem nao encontrada para testar anexo.");

            UsuarioDAO.excluirUsuario(autor.getidUsuario());

            return;

        }

        // INSERIR

        Anexo anexo = new Anexo();

        String nomeArquivoTeste = "exemplo.pdf";

        anexo.setNomeArquivo(nomeArquivoTeste);

        anexo.setTipo("application/pdf");

        anexo.setUrl("/caminho/para/exemplo.pdf");

        anexo.setPostagem(postagem);

        AnexoDAO.inserirAnexo(anexo);

        // LISTAR

        System.out.println("--------LISTA DE ANEXOS--------");

        ArrayList<Anexo> anexos = AnexoDAO.listarAnexos();

        for (Anexo a : anexos) {

            if (a.getNomeArquivo().equals(nomeArquivoTeste)) {

                anexo.setidAnexo(a.getidAnexo());

            }

            System.out.println("ID: " + a.getidAnexo());

            System.out.println("Nome do Arquivo: " + a.getNomeArquivo());

            System.out.println("Tipo: " + a.getTipo());

            System.out.println("URL: " + a.getUrl());

            System.out.println("-----------------------");

        }

        if (anexo.getidAnexo() == 0) {

            System.out.println("Anexo inserido nao encontrado para alterar/excluir.");

            PostagemDAO.excluirPostagem(postagem.getidPostagem());

            UsuarioDAO.excluirUsuario(autor.getidUsuario());

            return;

        }

        // ALTERAR

        anexo.setNomeArquivo("exemplo_atualizado.pdf");

        anexo.setTipo("application/pdf");

        anexo.setUrl("/novo/caminho/exemplo.pdf");

        AnexoDAO.alterarAnexo(anexo);

        // EXCLUIR

        AnexoDAO.excluirAnexo(anexo.getidAnexo());

        PostagemDAO.excluirPostagem(postagem.getidPostagem());

        UsuarioDAO.excluirUsuario(autor.getidUsuario());

    }
}
