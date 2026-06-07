package com.SkillShare.SkillShare.Persistencia;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.time.LocalDateTime;

import java.util.ArrayList;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.SkillShare.SkillShare.Modelo.Mensagem;

import com.SkillShare.SkillShare.Modelo.Usuario;

// DAO responsavel por mensagens, conexoes e criptografia do texto das mensagens.
public class MensagemDAO {

    private static String chaveAes = null;

    // Recebe a chave AES configurada pela aplicacao para cifrar e decifrar mensagens.
    public static void setChaveAes(String chave) {
        chaveAes = chave;
    }

    // Cifra textos de mensagens normais antes de salvar no banco.
    private static String cifrar(String texto) {
        if (chaveAes == null || texto == null) return texto;
        try {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(chaveAes), "AES"),
                   new GCMParameterSpec(128, iv));
            byte[] enc = c.doFinal(texto.getBytes("UTF-8"));
            ByteBuffer buf = ByteBuffer.allocate(12 + enc.length);
            buf.put(iv); buf.put(enc);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            return texto;
        }
    }

    // Decifra mensagens salvas; se for texto antigo ou invalido, devolve como estava.
    private static String decifrar(String cifrado) {
        if (chaveAes == null || cifrado == null) return cifrado;
        try {
            byte[] dados = Base64.getDecoder().decode(cifrado);
            if (dados.length < 12) return cifrado;
            ByteBuffer buf = ByteBuffer.wrap(dados);
            byte[] iv = new byte[12]; buf.get(iv);
            byte[] enc = new byte[buf.remaining()]; buf.get(enc);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(chaveAes), "AES"),
                   new GCMParameterSpec(128, iv));
            return new String(c.doFinal(enc), "UTF-8");
        } catch (Exception e) {
            return cifrado;
        }
    }

    // Lista todas as mensagens, monta remetente/destinatario e decifra apenas mensagens do tipo MENSAGEM.
    public static ArrayList<Mensagem> listarMensagens() throws SQLException {

        ArrayList<Mensagem> mensagens = new ArrayList<>();

        String sql = "SELECT * FROM mensagem";

        try (Connection cn = Conexao.getConnection();

             Statement st = cn.createStatement();

             ResultSet rs = st.executeQuery(sql)) {

            ArrayList<Usuario> usuarios = UsuarioDAO.listarUsuarios();

            while (rs.next()) {

                int idRemetente = rs.getInt("idRemetente");

                int idDestinatario = rs.getInt("idDestinatario");

                Usuario remetente = usuarios.stream()

                        .filter(u -> u.getIdUsuario() == idRemetente)

                        .findFirst().orElse(null);

                Usuario destinatario = usuarios.stream()

                        .filter(u -> u.getIdUsuario() == idDestinatario)

                        .findFirst().orElse(null);

                String textoDb = rs.getString("texto");
                String tipoDb  = rs.getString("tipo");


                if ("MENSAGEM".equals(tipoDb)) {
                    textoDb = decifrar(textoDb);
                }

                Mensagem m = new Mensagem(
                        rs.getObject("dataEnvio", LocalDateTime.class),
                        destinatario,
                        rs.getInt("idMensagem"),
                        rs.getBoolean("lida"),
                        remetente,
                        textoDb
                );

                m.setTipo(tipoDb);

                mensagens.add(m);
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar mensagens: " + e.getMessage());

            throw e;
        }

        return mensagens;
    }

    // Insere uma mensagem, cifrando o texto quando for conversa normal.
    public static void inserirMensagem(Mensagem mensagem) throws SQLException {

        String sql = "INSERT INTO mensagem (texto, dataEnvio, lida, idRemetente, idDestinatario, tipo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            String tipoMsg = mensagem.getTipo() != null ? mensagem.getTipo() : "MENSAGEM";

            String textoParaSalvar = "MENSAGEM".equals(tipoMsg)
                    ? cifrar(mensagem.getTexto())
                    : mensagem.getTexto();

            ps.setString(1, textoParaSalvar);

            ps.setObject(2, mensagem.getDataEnvio());

            ps.setBoolean(3, mensagem.isLida());

            ps.setInt(4, mensagem.getRemetente().getIdUsuario());

            ps.setInt(5, mensagem.getDestinatario().getIdUsuario());

            ps.setString(6, tipoMsg);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao inserir mensagem: " + e.getMessage());

            throw e;
        }
    }

    // Percorre mensagens antigas em texto puro e grava novamente usando criptografia.
    public static void migrarCriptografia() throws SQLException {

        String sqlSelect = "SELECT idMensagem, texto FROM mensagem WHERE tipo = 'MENSAGEM'";
        String sqlUpdate = "UPDATE mensagem SET texto = ? WHERE idMensagem = ?";

        try (Connection cn = Conexao.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sqlSelect)) {

            try (PreparedStatement ps = cn.prepareStatement(sqlUpdate)) {

                while (rs.next()) {

                    int id    = rs.getInt("idMensagem");
                    String tx = rs.getString("texto");

                    if (tx == null) continue;


                    String tentativa = decifrar(tx);

                    if (tentativa.equals(tx)) {

                        ps.setString(1, cifrar(tx));
                        ps.setInt(2, id);
                        ps.addBatch();
                    }
                }

                ps.executeBatch();
            }
        }
    }

    // Atualiza somente o tipo da mensagem, usado para aceitar conexoes pendentes.
    public static void atualizarTipo(int idMensagem, String tipo) throws SQLException {

        String sql = "UPDATE mensagem SET tipo = ? WHERE idMensagem = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, tipo);

            ps.setInt(2, idMensagem);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao atualizar tipo de mensagem: " + e.getMessage());

            throw e;
        }
    }

    // Atualiza os campos principais de uma mensagem existente.
    public static void alterarMensagem(Mensagem mensagem) throws SQLException {

        String sql = "UPDATE mensagem SET texto = ?, dataEnvio = ?, lida = ?, idRemetente = ?, idDestinatario = ? WHERE idMensagem = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            String tipoMsg = mensagem.getTipo() != null ? mensagem.getTipo() : "MENSAGEM";
            String textoParaSalvar = "MENSAGEM".equals(tipoMsg) ? cifrar(mensagem.getTexto()) : mensagem.getTexto();
            ps.setString(1, textoParaSalvar);

            ps.setObject(2, mensagem.getDataEnvio());

            ps.setBoolean(3, mensagem.isLida());

            ps.setInt(4, mensagem.getRemetente().getIdUsuario());

            ps.setInt(5, mensagem.getDestinatario().getIdUsuario());

            ps.setInt(6, mensagem.getIdMensagem());

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao alterar mensagem: " + e.getMessage());

            throw e;
        }
    }

    // Remove uma mensagem pelo ID.
    public static void excluirMensagem(int id) throws SQLException {

        String sql = "DELETE FROM mensagem WHERE idMensagem = ?";

        try (Connection cn = Conexao.getConnection();

             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println("Erro ao excluir mensagem: " + e.getMessage());

            throw e;
        }
    }
}
