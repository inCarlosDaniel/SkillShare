package com.SkillShare.SkillShare.Teste;

import com.SkillShare.SkillShare.Modelo.Material;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.MaterialDAO;
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.rowset.serial.SerialBlob;

public class TesteMaterial {

    public static void main(String[] args) throws SQLException {

        // Cria o usuário autor que será vinculado ao material testado
        Usuario autor = new Usuario();

        String emailAutor = "autor.material@email.com"; // e-mail único para identificar o autor durante o teste

        autor.setNome("Maria Lorde"); // define o nome do autor de teste

        autor.setEmail(emailAutor); // define o e-mail do autor de teste

        autor.setSenha("123456"); // define a senha do autor de teste

        autor.setHabilidades("Java"); // define as habilidades do autor de teste

        autor.setInteresses("Materiais"); // define os interesses do autor de teste

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

            System.out.println("Usuario autor nao encontrado para testar material."); // informa falha na recuperação do autor

            return; // encerra o teste sem prosseguir

        }


        // INSERIR
        Material material = new Material(); // cria o objeto de material a ser testado

        String tituloTeste = "Apostila de Java"; // título usado para identificar o material durante o teste

        material.setTitulo(tituloTeste); // define o título do material de teste

        material.setCategoria("Programacao"); // define a categoria do material de teste

        material.setArquivo(new SerialBlob("Conteudo do arquivo".getBytes())); // cria um Blob com o conteúdo binário do arquivo de teste

        material.setAutor(autor); // vincula o autor previamente inserido ao material

        MaterialDAO.inserirMaterial(material); // persiste o material no banco de dados


        // LISTAR
        System.out.println("--------LISTA DE MATERIAIS--------"); // exibe cabeçalho da listagem de materiais

        ArrayList<Material> materiais = MaterialDAO.listarMateriais(); // recupera todos os materiais cadastrados no banco

        for (Material m : materiais) { // percorre a lista de materiais para exibir e localizar o inserido

            if (m.getTitulo().equals(tituloTeste)) { // identifica o material inserido pelo título

                material.setidMaterial(m.getidMaterial()); // atribui o ID gerado pelo banco ao objeto local

            }

            System.out.println("ID: " + m.getidMaterial()); // exibe o ID do material

            System.out.println("Titulo: " + m.getTitulo()); // exibe o título do material

            System.out.println("Categoria: " + m.getCategoria()); // exibe a categoria do material

            if (m.getAutor() != null) { // verifica se o autor está associado ao material

                System.out.println("Autor: " + m.getAutor().getNome()); // exibe o nome do autor do material

            } else {

                System.out.println("Autor: Nao encontrado"); // informa que o autor não foi carregado

            }

            System.out.println("Arquivo: " + m.getArquivo()); // exibe a referência ao arquivo binário do material

            System.out.println("-----------------------"); // separador visual entre registros
        }

        if (material.getidMaterial() == 0) { // verifica se o material foi encontrado após a inserção

            System.out.println("Material inserido nao encontrado para alterar/excluir."); // informa que o ID não foi recuperado

            UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário de suporte para limpar o teste

            return; // encerra o teste sem prosseguir para alteração e exclusão

        }


        // ALTERAR
        material.setTitulo("Apostila de Java Atualizada"); // atualiza o título do material

        material.setCategoria("Desenvolvimento"); // atualiza a categoria do material

        material.setArquivo(new SerialBlob("Conteudo atualizado do arquivo".getBytes())); // atualiza o conteúdo binário do arquivo

        MaterialDAO.alterarMaterial(material); // persiste as alterações do material no banco de dados


        // EXCLUIR
        MaterialDAO.excluirMaterial(material.getidMaterial()); // remove o material do banco de dados pelo seu ID

        UsuarioDAO.excluirUsuario(autor.getIdUsuario()); // remove o usuário autor de suporte usado no teste
    }
}
