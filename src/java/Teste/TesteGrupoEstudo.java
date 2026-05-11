package Teste;

import Modelo.GrupoEstudo;
import Modelo.Usuario;
import Persistencia.GrupoEstudoDAO;
import Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.ArrayList;

public class TesteGrupoEstudo {

    public static void main(String[] args) throws SQLException {


        Usuario criador = new Usuario();

        String emailCriador = "criador.grupo@email.com";

        criador.setNome("Rayane Limas");

        criador.setEmail(emailCriador);

        criador.setSenha("123456");

        criador.setHabilidades("Java");

        criador.setInteresses("Grupos de estudo");

        criador.setDificuldades("Nenhuma");

        UsuarioDAO.inserirUsuario(criador);

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios();

        for (Usuario u : usuarios) {

            if (u.getEmail().equals(emailCriador)) {

                criador.setidUsuario(u.getidUsuario());

                break;

            }

        }

        if (criador.getidUsuario() == 0) {

            System.out.println("Usuario criador nao encontrado para testar grupo de estudo.");

            return;

        }


        // INSERIR


        GrupoEstudo grupo = new GrupoEstudo();

        String nomeTeste = "Grupo de Java";

        grupo.setNome(nomeTeste);


        grupo.setMateria("Programacao");


        grupo.setExameAlvo("Projeto Final");


        grupo.setCriador(criador);


        grupo.setMembros(new ArrayList<>());


        GrupoEstudoDAO.inserirGrupoEstudo(grupo);



        // LISTAR

        System.out.println("--------LISTA DE GRUPOS DE ESTUDO--------");


        ArrayList<GrupoEstudo> grupos = GrupoEstudoDAO.listarGruposEstudo();


        for (GrupoEstudo g : grupos) {

            if (g.getNome().equals(nomeTeste)) {

                grupo.setidGrupo(g.getidGrupo());

            }


            System.out.println("ID: " + g.getidGrupo());


            System.out.println("Nome: " + g.getNome());
            

            System.out.println("Materia: " + g.getMateria());


            System.out.println("Exame Alvo: " + g.getExameAlvo());


            if (g.getCriador() != null) {


                System.out.println("Criador: " + g.getCriador().getNome());


            } else {


                System.out.println("Criador: Nao encontrado");


            }


            System.out.println("-----------------------");

        }

        if (grupo.getidGrupo() == 0) {

            System.out.println("Grupo de estudo inserido nao encontrado para alterar/excluir.");

            UsuarioDAO.excluirUsuario(criador.getidUsuario());

            return;

        }


        // ALTERAR

        grupo.setNome("Grupo de Java Atualizado");


        grupo.setMateria("Desenvolvimento Web");


        grupo.setExameAlvo("Projeto Final Atualizado");


        GrupoEstudoDAO.alterarGrupoEstudo(grupo);


        // EXCLUIR

        GrupoEstudoDAO.excluirGrupoEstudo(grupo.getidGrupo());

        UsuarioDAO.excluirUsuario(criador.getidUsuario());
        
    }

}
