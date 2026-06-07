package com.SkillShare.SkillShare.Teste;

import com.SkillShare.SkillShare.Modelo.GrupoEstudo;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.GrupoEstudoDAO;
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.ArrayList;

public class TesteGrupoEstudo {

    public static void main(String[] args) throws SQLException {


        // Cria o usuário que será o criador do grupo de estudo testado
        Usuario criador = new Usuario();

        String emailCriador = "criador.grupo@email.com"; // e-mail único para identificar o criador durante o teste

        criador.setNome("Rayane Limas"); // define o nome do criador de teste

        criador.setEmail(emailCriador); // define o e-mail do criador de teste

        criador.setSenha("123456"); // define a senha do criador de teste

        criador.setHabilidades("Java"); // define as habilidades do criador de teste

        criador.setInteresses("Grupos de estudo"); // define os interesses do criador de teste

        criador.setDificuldades("Nenhuma"); // define as dificuldades do criador de teste

        UsuarioDAO.inserirUsuario(criador); // persiste o usuário criador no banco de dados

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios(); // recupera todos os usuários para localizar o ID gerado

        for (Usuario u : usuarios) { // percorre a lista para encontrar o criador pelo e-mail

            if (u.getEmail().equals(emailCriador)) { // compara o e-mail para identificar o registro inserido

                criador.setIdUsuario(u.getIdUsuario()); // atribui o ID gerado pelo banco ao objeto local

                break; // interrompe a busca ao encontrar o registro

            }

        }

        if (criador.getIdUsuario() == 0) { // verifica se o criador foi encontrado após a inserção

            System.out.println("Usuario criador nao encontrado para testar grupo de estudo."); // informa falha na recuperação do criador

            return; // encerra o teste sem prosseguir

        }


        // INSERIR


        GrupoEstudo grupo = new GrupoEstudo(); // cria o objeto de grupo de estudo a ser testado

        String nomeTeste = "Grupo de Java"; // nome do grupo usado para identificar o registro durante o teste

        grupo.setNome(nomeTeste); // define o nome do grupo de estudo


        grupo.setMateria("Programacao"); // define a matéria associada ao grupo de estudo


        grupo.setExameAlvo("Projeto Final"); // define o exame ou objetivo alvo do grupo


        grupo.setCriador(criador); // vincula o usuário criador ao grupo de estudo


        grupo.setMembros(new ArrayList<>()); // inicializa a lista de membros do grupo como vazia


        GrupoEstudoDAO.inserirGrupoEstudo(grupo); // persiste o grupo de estudo no banco de dados



        // LISTAR

        System.out.println("--------LISTA DE GRUPOS DE ESTUDO--------"); // exibe cabeçalho da listagem de grupos


        ArrayList<GrupoEstudo> grupos = GrupoEstudoDAO.listarGruposEstudo(); // recupera todos os grupos cadastrados no banco


        for (GrupoEstudo g : grupos) { // percorre a lista de grupos para exibir e localizar o inserido

            if (g.getNome().equals(nomeTeste)) { // identifica o grupo inserido pelo nome

                grupo.setidGrupo(g.getidGrupo()); // atribui o ID gerado pelo banco ao objeto local

            }


            System.out.println("ID: " + g.getidGrupo()); // exibe o ID do grupo de estudo


            System.out.println("Nome: " + g.getNome()); // exibe o nome do grupo de estudo


            System.out.println("Materia: " + g.getMateria()); // exibe a matéria do grupo de estudo


            System.out.println("Exame Alvo: " + g.getExameAlvo()); // exibe o exame alvo do grupo de estudo


            if (g.getCriador() != null) { // verifica se o criador está associado ao grupo


                System.out.println("Criador: " + g.getCriador().getNome()); // exibe o nome do criador do grupo


            } else {


                System.out.println("Criador: Nao encontrado"); // informa que o criador não foi carregado


            }


            System.out.println("-----------------------"); // separador visual entre registros

        }

        if (grupo.getidGrupo() == 0) { // verifica se o grupo foi encontrado após a inserção

            System.out.println("Grupo de estudo inserido nao encontrado para alterar/excluir."); // informa que o ID não foi recuperado

            UsuarioDAO.excluirUsuario(criador.getIdUsuario()); // remove o usuário de suporte para limpar o teste

            return; // encerra o teste sem prosseguir para alteração e exclusão

        }


        // ALTERAR

        grupo.setNome("Grupo de Java Atualizado"); // atualiza o nome do grupo de estudo


        grupo.setMateria("Desenvolvimento Web"); // atualiza a matéria do grupo de estudo


        grupo.setExameAlvo("Projeto Final Atualizado"); // atualiza o exame alvo do grupo de estudo


        GrupoEstudoDAO.alterarGrupoEstudo(grupo); // persiste as alterações do grupo no banco de dados


        // EXCLUIR

        GrupoEstudoDAO.excluirGrupoEstudo(grupo.getidGrupo()); // remove o grupo de estudo do banco de dados pelo seu ID

        UsuarioDAO.excluirUsuario(criador.getIdUsuario()); // remove o usuário criador de suporte usado no teste

    }

}
