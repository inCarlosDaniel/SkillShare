package Teste;

import Modelo.Material;
import Modelo.Usuario;
import Persistencia.MaterialDAO;
import Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.rowset.serial.SerialBlob;

public class TesteMaterial {

    public static void main(String[] args) throws SQLException {

        Usuario autor = new Usuario();

        String emailAutor = "autor.material@email.com";

        autor.setNome("Maria Lorde");

        autor.setEmail(emailAutor);

        autor.setSenha("123456");

        autor.setHabilidades("Java");

        autor.setInteresses("Materiais");

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

            System.out.println("Usuario autor nao encontrado para testar material.");

            return;

        }


        // INSERIR
        Material material = new Material();

        String tituloTeste = "Apostila de Java";

        material.setTitulo(tituloTeste);

        material.setCategoria("Programacao");

        material.setArquivo(new SerialBlob("Conteudo do arquivo".getBytes()));

        material.setAutor(autor);

        MaterialDAO.inserirMaterial(material);


        // LISTAR
        System.out.println("--------LISTA DE MATERIAIS--------");

        ArrayList<Material> materiais = MaterialDAO.listarMateriais();

        for (Material m : materiais) {

            if (m.getTitulo().equals(tituloTeste)) {

                material.setidMaterial(m.getidMaterial());

            }

            System.out.println("ID: " + m.getidMaterial());

            System.out.println("Titulo: " + m.getTitulo());

            System.out.println("Categoria: " + m.getCategoria());

            if (m.getAutor() != null) {

                System.out.println("Autor: " + m.getAutor().getNome());

            } else {

                System.out.println("Autor: Nao encontrado");

            }

            System.out.println("Arquivo: " + m.getArquivo());

            System.out.println("-----------------------");
        }

        if (material.getidMaterial() == 0) {

            System.out.println("Material inserido nao encontrado para alterar/excluir.");

            UsuarioDAO.excluirUsuario(autor.getidUsuario());

            return;

        }


        // ALTERAR
        material.setTitulo("Apostila de Java Atualizada");

        material.setCategoria("Desenvolvimento");

        material.setArquivo(new SerialBlob("Conteudo atualizado do arquivo".getBytes()));

        MaterialDAO.alterarMaterial(material);


        // EXCLUIR
        MaterialDAO.excluirMaterial(material.getidMaterial());

        UsuarioDAO.excluirUsuario(autor.getidUsuario());
    }
}
