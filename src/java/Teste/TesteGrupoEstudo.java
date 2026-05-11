package Teste;

import Modelo.GrupoEstudo;
import Persistencia.GrupoEstudoDAO;
import java.sql.SQLException;
import java.util.ArrayList;

public class TesteGrupoEstudo {

    public static void main(String[] args) throws SQLException {


        // INSERIR


        GrupoEstudo grupo = new GrupoEstudo();

        String nomeTeste = "Grupo de Java";

        grupo.setNome(nomeTeste);


        grupo.setMateria("Programacao");


        grupo.setExameAlvo("Projeto Final");


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

            return;

        }


        // ALTERAR

        grupo.setNome("Grupo de Java Atualizado");


        grupo.setMateria("Desenvolvimento Web");


        grupo.setExameAlvo("Projeto Final Atualizado");


        GrupoEstudoDAO.alterarGrupoEstudo(grupo);


        // EXCLUIR

        GrupoEstudoDAO.excluirGrupoEstudo(grupo.getidGrupo());
        
    }

}
