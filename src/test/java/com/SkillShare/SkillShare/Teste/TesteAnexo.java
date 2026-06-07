package com.SkillShare.SkillShare.Teste;

import com.SkillShare.SkillShare.Modelo.Anexo;
import com.SkillShare.SkillShare.Modelo.Postagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.AnexoDAO;
import com.SkillShare.SkillShare.Persistencia.PostagemDAO;
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TesteAnexo {

    public static void main(String[] args) throws SQLException {

        // Cria o usuário autor que será responsável pela postagem vinculada ao anexo
        Usuario autor = new Usuario();

        String emailAutor = "autor.anexo@email.com"; // e-mail único para identificar o autor durante o teste

        autor.setNome("Autor Anexo"); // define o nome do autor de teste

        autor.setEmail(emailAutor); // define o e-mail do autor de teste

        autor.setSenha("123456"); // define a senha do autor de teste

        autor.setHabilidades("Java"); // define as habilidades do autor de teste

        autor.setInteresses("Anexos"); // define os interesses do autor de teste

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

            System.out.println("Usuario autor nao encontrado para testar anexo."); // informa falha na recuperação do autor

            return; // encerra o teste sem prosseguir

        }

        // Cria a postagem que servirá como vínculo para o anexo testado
        Postagem postagem = new Postagem();

        String conteudoPostagem = "Postagem para anexo"; // conteúdo textual da postagem de suporte ao teste

        postagem.setConteudo(conteudoPostagem); // define o conteúdo da postagem de teste

        postagem.setAutor(autor); // vincula o autor previamente inserido à postagem

        postagem.setDataPublicacao(LocalDateTime.now()); // define a data de publicação com o momento atual

        postagem.setAnexos(new ArrayList<>()); // inicializa a lista de anexos da postagem como vazia

        PostagemDAO.inserirPostagem(postagem); // persiste a postagem no banco de dados

        ArrayList<Postagem> postagens = PostagemDAO.listarPostagens(); // recupera todas as postagens para localizar o ID gerado

        for (Postagem p : postagens) { // percorre a lista para encontrar a postagem pelo conteúdo

            if (p.getConteudo().equals(conteudoPostagem)) { // compara o conteúdo para identificar a postagem inserida

                postagem.setidPostagem(p.getidPostagem()); // atribui o ID gerado pelo banco ao objeto local

                break; // interrompe a busca ao encontrar a postagem

            }

        }

        if (postagem.getidPostagem() == 0) { // verifica se a postagem foi encontrada após a inserção

            System.out.println("Postagem nao encontrada para testar anexo."); // informa falha na recuperação da postagem

            UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário para limpar os dados do teste

            return; // encerra o teste sem prosseguir

        }

        // INSERIR

        Anexo anexo = new Anexo(); // cria o objeto de anexo a ser testado

        String nomeArquivoTeste = "exemplo.pdf"; // nome do arquivo usado para identificar o anexo durante o teste

        anexo.setNomeArquivo(nomeArquivoTeste); // define o nome do arquivo do anexo

        anexo.setTipo("application/pdf"); // define o tipo MIME do arquivo anexado

        anexo.setUrl("/caminho/para/exemplo.pdf"); // define a URL de acesso ao arquivo anexado

        anexo.setPostagem(postagem); // vincula o anexo à postagem criada anteriormente

        AnexoDAO.inserirAnexo(anexo); // persiste o anexo no banco de dados

        // LISTAR

        System.out.println("--------LISTA DE ANEXOS--------"); // exibe cabeçalho da listagem de anexos

        ArrayList<Anexo> anexos = AnexoDAO.listarAnexos(); // recupera todos os anexos cadastrados no banco

        for (Anexo a : anexos) { // percorre a lista de anexos para exibir e localizar o inserido

            if (a.getNomeArquivo().equals(nomeArquivoTeste)) { // identifica o anexo inserido pelo nome do arquivo

                anexo.setidAnexo(a.getidAnexo()); // atribui o ID gerado pelo banco ao objeto local

            }

            System.out.println("ID: " + a.getidAnexo()); // exibe o ID do anexo

            System.out.println("Nome do Arquivo: " + a.getNomeArquivo()); // exibe o nome do arquivo do anexo

            System.out.println("Tipo: " + a.getTipo()); // exibe o tipo MIME do anexo

            System.out.println("URL: " + a.getUrl()); // exibe a URL do arquivo do anexo

            System.out.println("-----------------------"); // separador visual entre registros

        }

        if (anexo.getidAnexo() == 0) { // verifica se o anexo foi encontrado após a inserção

            System.out.println("Anexo inserido nao encontrado para alterar/excluir."); // informa que o ID não foi recuperado

            PostagemDAO.excluirPostagem(postagem.getidPostagem()); // remove a postagem de suporte para limpar o teste

            UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário de suporte para limpar o teste

            return; // encerra o teste sem prosseguir para alteração e exclusão

        }

        // ALTERAR

        anexo.setNomeArquivo("exemplo_atualizado.pdf"); // atualiza o nome do arquivo do anexo

        anexo.setTipo("application/pdf"); // mantém o tipo MIME do arquivo

        anexo.setUrl("/novo/caminho/exemplo.pdf"); // atualiza a URL de acesso ao arquivo anexado

        AnexoDAO.alterarAnexo(anexo); // persiste as alterações do anexo no banco de dados

        // EXCLUIR

        AnexoDAO.excluirAnexo(anexo.getidAnexo()); // remove o anexo do banco de dados pelo seu ID

        PostagemDAO.excluirPostagem(postagem.getidPostagem()); // remove a postagem de suporte usada no teste

        UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário de suporte usado no teste

    }
}
