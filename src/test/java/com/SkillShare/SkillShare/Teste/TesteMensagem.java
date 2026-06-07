package com.SkillShare.SkillShare.Teste;

import com.SkillShare.SkillShare.Modelo.Mensagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.MensagemDAO;
import com.SkillShare.SkillShare.Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TesteMensagem {

    public static void main(String[] args) throws SQLException {

        // Cria o usuário remetente da mensagem de teste
        Usuario remetente = new Usuario();

        String emailRemetente = "remetente@email.com"; // e-mail único para identificar o remetente durante o teste

        remetente.setNome("Maria Lorde"); // define o nome do remetente de teste

        remetente.setEmail(emailRemetente); // define o e-mail do remetente de teste

        remetente.setSenha("123456"); // define a senha do remetente de teste

        remetente.setHabilidades("Java"); // define as habilidades do remetente de teste

        remetente.setInteresses("Mensagens"); // define os interesses do remetente de teste

        remetente.setDificuldades("Nenhuma"); // define as dificuldades do remetente de teste

        // Cria o usuário destinatário da mensagem de teste
        Usuario destinatario = new Usuario();

        String emailDestinatario = "destinatario@email.com"; // e-mail único para identificar o destinatário durante o teste

        destinatario.setNome("Felipe Gomes"); // define o nome do destinatário de teste

        destinatario.setEmail(emailDestinatario); // define o e-mail do destinatário de teste

        destinatario.setSenha("123456"); // define a senha do destinatário de teste

        destinatario.setHabilidades("MySQL"); // define as habilidades do destinatário de teste

        destinatario.setInteresses("Mensagens"); // define os interesses do destinatário de teste

        destinatario.setDificuldades("Nenhuma"); // define as dificuldades do destinatário de teste

        UsuarioDAO.inserirUsuario(remetente); // persiste o usuário remetente no banco de dados

        UsuarioDAO.inserirUsuario(destinatario); // persiste o usuário destinatário no banco de dados

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios(); // recupera todos os usuários para localizar os IDs gerados

        for (Usuario u : usuarios) { // percorre a lista para encontrar remetente e destinatário pelos e-mails

            if (u.getEmail().equals(emailRemetente)) { // identifica o remetente pelo e-mail

                remetente.setIdUsuario(u.getIdUsuario()); // atribui o ID gerado pelo banco ao remetente

            }

            if (u.getEmail().equals(emailDestinatario)) { // identifica o destinatário pelo e-mail

                destinatario.setIdUsuario(u.getIdUsuario()); // atribui o ID gerado pelo banco ao destinatário

            }

        }

        if (remetente.getIdUsuario() == 0 || destinatario.getIdUsuario() == 0) { // verifica se ambos os usuários foram encontrados

            System.out.println("Usuarios da mensagem nao encontrados."); // informa falha na recuperação dos usuários

            return; // encerra o teste sem prosseguir

        }


        // INSERIR
        Mensagem mensagem = new Mensagem(); // cria o objeto de mensagem a ser testado

        String textoTeste = "Mensagem de teste"; // texto usado para identificar a mensagem durante o teste

        mensagem.setTexto(textoTeste); // define o conteúdo textual da mensagem

        mensagem.setDataEnvio(LocalDateTime.now()); // define a data e hora de envio com o momento atual

        mensagem.setLida(false); // marca a mensagem como não lida inicialmente

        mensagem.setRemetente(remetente); // vincula o remetente à mensagem

        mensagem.setDestinatario(destinatario); // vincula o destinatário à mensagem

        MensagemDAO.inserirMensagem(mensagem); // persiste a mensagem no banco de dados


        // LISTAR
        System.out.println("=== LISTA DE MENSAGENS ==="); // exibe cabeçalho da listagem de mensagens

        ArrayList<Mensagem> mensagens = MensagemDAO.listarMensagens(); // recupera todas as mensagens cadastradas no banco

        for (Mensagem m : mensagens) { // percorre a lista de mensagens para exibir e localizar a inserida

            if (m.getTexto().equals(textoTeste)) { // identifica a mensagem inserida pelo texto

                mensagem.setIdMensagem(m.getIdMensagem()); // atribui o ID gerado pelo banco ao objeto local

            }

            System.out.println("ID: " + m.getIdMensagem()); // exibe o ID da mensagem

            System.out.println("Texto: " + m.getTexto()); // exibe o conteúdo textual da mensagem

            System.out.println("Data de Envio: " + m.getDataEnvio()); // exibe a data e hora de envio da mensagem

            System.out.println("Lida: " + m.isLida()); // exibe se a mensagem foi lida ou não

            if (m.getRemetente() != null) { // verifica se o remetente está associado à mensagem

                System.out.println("Remetente: " + m.getRemetente().getNome()); // exibe o nome do remetente da mensagem

            } else {

                System.out.println("Remetente: Nao encontrado"); // informa que o remetente não foi carregado

            }

            if (m.getDestinatario() != null) { // verifica se o destinatário está associado à mensagem

                System.out.println("Destinatario: " + m.getDestinatario().getNome()); // exibe o nome do destinatário da mensagem

            } else {

                System.out.println("Destinatario: Nao encontrado"); // informa que o destinatário não foi carregado

            }

            System.out.println("-----------------------"); // separador visual entre registros
        }

        if (mensagem.getIdMensagem() == 0) { // verifica se a mensagem foi encontrada após a inserção

            System.out.println("Mensagem inserida nao encontrada para alterar/excluir."); // informa que o ID não foi recuperado

            UsuarioDAO.excluirUsuario(remetente.getIdUsuario()); // remove o remetente de suporte para limpar o teste

            UsuarioDAO.excluirUsuario(destinatario.getIdUsuario()); // remove o destinatário de suporte para limpar o teste

            return; // encerra o teste sem prosseguir para alteração e exclusão

        }


        // ALTERAR
        mensagem.setTexto("Mensagem de teste atualizada."); // atualiza o conteúdo textual da mensagem

        mensagem.setDataEnvio(LocalDateTime.now()); // atualiza a data de envio com o momento atual

        mensagem.setLida(true); // marca a mensagem como lida após a atualização

        MensagemDAO.alterarMensagem(mensagem); // persiste as alterações da mensagem no banco de dados


        // EXCLUIR
        MensagemDAO.excluirMensagem(mensagem.getIdMensagem()); // remove a mensagem do banco de dados pelo seu ID

        UsuarioDAO.excluirUsuario(remetente.getIdUsuario()); // remove o remetente de suporte usado no teste

        UsuarioDAO.excluirUsuario(destinatario.getIdUsuario()); // remove o destinatário de suporte usado no teste
    }
}
