package Teste;

import Modelo.Mensagem;
import Modelo.Usuario;
import Persistencia.MensagemDAO;
import Persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TesteMensagem {

    public static void main(String[] args) throws SQLException {

        Usuario remetente = new Usuario();

        String emailRemetente = "remetente@email.com";

        remetente.setNome("Maria Lorde");

        remetente.setEmail(emailRemetente);

        remetente.setSenha("123456");

        remetente.setHabilidades("Java");

        remetente.setInteresses("Mensagens");

        remetente.setDificuldades("Nenhuma");

        Usuario destinatario = new Usuario();

        String emailDestinatario = "destinatario@email.com";

        destinatario.setNome("Felipe Gomes");

        destinatario.setEmail(emailDestinatario);

        destinatario.setSenha("123456");

        destinatario.setHabilidades("MySQL");

        destinatario.setInteresses("Mensagens");

        destinatario.setDificuldades("Nenhuma");

        UsuarioDAO.inserirUsuario(remetente);

        UsuarioDAO.inserirUsuario(destinatario);

        ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios();

        for (Usuario u : usuarios) {

            if (u.getEmail().equals(emailRemetente)) {

                remetente.setidUsuario(u.getidUsuario());

            }

            if (u.getEmail().equals(emailDestinatario)) {

                destinatario.setidUsuario(u.getidUsuario());

            }

        }

        if (remetente.getidUsuario() == 0 || destinatario.getidUsuario() == 0) {

            System.out.println("Usuarios da mensagem nao encontrados.");

            return;

        }


        // INSERIR
        Mensagem mensagem = new Mensagem();

        String textoTeste = "Mensagem de teste";

        mensagem.setTexto(textoTeste);

        mensagem.setDataEnvio(LocalDateTime.now());

        mensagem.setLida(false);

        mensagem.setRemetente(remetente);

        mensagem.setDestinatario(destinatario);

        MensagemDAO.inserirMensagem(mensagem);


        // LISTAR
        System.out.println("=== LISTA DE MENSAGENS ===");

        ArrayList<Mensagem> mensagens = MensagemDAO.listarMensagens();

        for (Mensagem m : mensagens) {

            if (m.getTexto().equals(textoTeste)) {

                mensagem.setidMensagem(m.getidMensagem());

            }

            System.out.println("ID: " + m.getidMensagem());

            System.out.println("Texto: " + m.getTexto());

            System.out.println("Data de Envio: " + m.getDataEnvio());

            System.out.println("Lida: " + m.isLida());

            if (m.getRemetente() != null) {

                System.out.println("Remetente: " + m.getRemetente().getNome());

            } else {

                System.out.println("Remetente: Nao encontrado");

            }

            if (m.getDestinatario() != null) {

                System.out.println("Destinatario: " + m.getDestinatario().getNome());

            } else {

                System.out.println("Destinatario: Nao encontrado");

            }

            System.out.println("-----------------------");
        }

        if (mensagem.getidMensagem() == 0) {

            System.out.println("Mensagem inserida nao encontrada para alterar/excluir.");

            UsuarioDAO.excluirUsuario(remetente.getidUsuario());

            UsuarioDAO.excluirUsuario(destinatario.getidUsuario());

            return;

        }


        // ALTERAR
        mensagem.setTexto("Mensagem de teste atualizada.");

        mensagem.setDataEnvio(LocalDateTime.now());

        mensagem.setLida(true);

        MensagemDAO.alterarMensagem(mensagem);


        // EXCLUIR
        MensagemDAO.excluirMensagem(mensagem.getidMensagem());

        UsuarioDAO.excluirUsuario(remetente.getidUsuario());

        UsuarioDAO.excluirUsuario(destinatario.getidUsuario());
    }
}
