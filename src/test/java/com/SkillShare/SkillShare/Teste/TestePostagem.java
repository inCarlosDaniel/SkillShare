package com.SkillShare.SkillShare.Teste;

import com.SkillShare.SkillShare.Modelo.Postagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.PostagemDAO;
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestePostagem {

    public static void main(String[] args) throws SQLException {

        // Cria o usuário autor que será vinculado à postagem testada
        Usuario autor = new Usuario();

        String emailAutor = "autor.postagem@email.com"; // e-mail único para identificar o autor durante o teste

        autor.setNome("Kauan Silva"); // define o nome do autor de teste

        autor.setEmail(emailAutor); // define o e-mail do autor de teste

        autor.setSenha("123456"); // define a senha do autor de teste

        autor.setHabilidades("Java"); // define as habilidades do autor de teste

        autor.setInteresses("Postagens"); // define os interesses do autor de teste

        autor.setDificuldades("Nenhuma"); // define as dificuldades do autor de teste

        UsuarioDAO.inserirUsuario(autor); // persiste o usuário autor no banco de dados

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios(); // recupera todos os usuários para localizar o ID gerado

        for (Usuario u : usuarios) { // percorre a lista para encontrar o autor pelo e-mail

            if (u.getEmail().equals(emailAutor)) { // compara o e-mail para identificar o registro inserido

                autor.setIdUsuario(u.getIdUsuario()); // atribui o ID gerado pelo banco ao objeto local

                break; // interrompe a busca ao encontrar o registro

            }

        }

        if (autor.getIdUsuario() == 0) { // verifica se o autor foi encontrado após a inserção

            System.out.println("Usuario autor nao encontrado para testar postagem."); // informa falha na recuperação do autor

            return; // encerra o teste sem prosseguir

        }


        // INSERIR
        Postagem postagem = new Postagem(); // cria o objeto de postagem a ser testado

        String conteudoTeste = "Conteudo da postagem de teste"; // conteúdo usado para identificar a postagem durante o teste

        postagem.setConteudo(conteudoTeste); // define o conteúdo textual da postagem

        postagem.setAutor(autor); // vincula o autor previamente inserido à postagem

        postagem.setDataPublicacao(LocalDateTime.now()); // define a data de publicação com o momento atual

        postagem.setAnexos(new ArrayList<>()); // inicializa a lista de anexos da postagem como vazia

        PostagemDAO.inserirPostagem(postagem); // persiste a postagem no banco de dados


        // LISTAR
        System.out.println("--------LISTA DE POSTAGENS--------"); // exibe cabeçalho da listagem de postagens

        ArrayList<Postagem> postagens = PostagemDAO.listarPostagens(); // recupera todas as postagens cadastradas no banco

        for (Postagem p : postagens) { // percorre a lista de postagens para exibir e localizar a inserida

            if (p.getConteudo().equals(conteudoTeste)) { // identifica a postagem inserida pelo conteúdo

                postagem.setidPostagem(p.getidPostagem()); // atribui o ID gerado pelo banco ao objeto local

            }

            System.out.println("ID: " + p.getidPostagem()); // exibe o ID da postagem

            System.out.println("Conteudo: " + p.getConteudo()); // exibe o conteúdo textual da postagem

            if (p.getAutor() != null) { // verifica se o autor está associado à postagem

                System.out.println("Autor: " + p.getAutor().getNome()); // exibe o nome do autor da postagem

            } else {

                System.out.println("Autor: Nao encontrado"); // informa que o autor não foi carregado

            }

            System.out.println("Data de Publicacao: " + p.getDataPublicacao()); // exibe a data de publicação da postagem

            System.out.println("-----------------------"); // separador visual entre registros
        }

        if (postagem.getidPostagem() == 0) { // verifica se a postagem foi encontrada após a inserção

            System.out.println("Postagem inserida nao encontrada para alterar/excluir."); // informa que o ID não foi recuperado

            UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário de suporte para limpar o teste

            return; // encerra o teste sem prosseguir para alteração e exclusão

        }


        // ALTERAR
        postagem.setConteudo("Conteudo da postagem atualizado."); // atualiza o conteúdo textual da postagem

        postagem.setDataPublicacao(LocalDateTime.now()); // atualiza a data de publicação com o momento atual

        PostagemDAO.alterarPostagem(postagem); // persiste as alterações da postagem no banco de dados


        // EXCLUIR
        PostagemDAO.excluirPostagem(postagem.getidPostagem()); // remove a postagem do banco de dados pelo seu ID

        UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário autor de suporte usado no teste
    }
}
