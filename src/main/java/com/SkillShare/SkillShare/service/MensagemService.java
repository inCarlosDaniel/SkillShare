package com.SkillShare.SkillShare.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.SkillShare.SkillShare.Modelo.Mensagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.MensagemDAO;

@Service
public class MensagemService {

    // Busca todas as mensagens salvas no banco e centraliza o tratamento de erro da consulta.
    public ArrayList<Mensagem> listarMensagens() {

        try {

            return MensagemDAO.listarMensagens();
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar mensagens", e);
        }
    }

    // Monta a lista de conversas do usuario, usando apenas a ultima mensagem trocada com cada parceiro.
    // Mensagens de conexao sao ignoradas para aparecerem somente conversas reais de chat.
    public List<Mensagem> listarConversas(int idUsuario) {

        var ultima = new LinkedHashMap<Integer, Mensagem>();

        listarMensagens().stream()

                .filter(m -> m.getRemetente() != null && m.getDestinatario() != null)

                .filter(m -> !m.isConexao() && !"Conexão iniciada".equals(m.getTexto()))

                .filter(m -> {

                    Integer r = m.getRemetente().getIdUsuario();

                    Integer d = m.getDestinatario().getIdUsuario();

                    return Integer.valueOf(idUsuario).equals(r) || Integer.valueOf(idUsuario).equals(d);
                })

                .sorted(Comparator.comparing(Mensagem::getDataEnvio,
                        Comparator.nullsFirst(Comparator.naturalOrder())))

                .forEach(m -> {

                    Integer r = m.getRemetente().getIdUsuario();

                    Integer partnerId = Integer.valueOf(idUsuario).equals(r)

                            ? m.getDestinatario().getIdUsuario()

                            : r;

                    // Como as mensagens foram ordenadas da mais antiga para a mais nova, a ultima sobrescreve as anteriores.
                    if (partnerId != null) ultima.put(partnerId, m);
                });

        List<Mensagem> resultado = new ArrayList<>(ultima.values());

        resultado.sort(Comparator.comparing(Mensagem::getDataEnvio,
                Comparator.nullsFirst(Comparator.reverseOrder())));

        return resultado;
    }

    // Retorna o historico completo entre dois usuarios, em ordem cronologica.
    public ArrayList<Mensagem> listarMensagensPorConversa(int id1, int id2) {

        return listarMensagens().stream()

                .filter(m -> m.getRemetente() != null && m.getDestinatario() != null)

                .filter(m -> !m.isConexao() && !"Conexão iniciada".equals(m.getTexto()))

                .filter(m -> {

                    Integer r = m.getRemetente().getIdUsuario();

                    Integer d = m.getDestinatario().getIdUsuario();

                    if (r == null || d == null) return false;

                    return (r.equals(id1) && d.equals(id2)) || (r.equals(id2) && d.equals(id1));
                })

                .sorted(Comparator.comparing(Mensagem::getDataEnvio,
                        Comparator.nullsFirst(Comparator.naturalOrder())))

                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Lista mensagens recebidas por um usuario, colocando as mais recentes primeiro.
    public ArrayList<Mensagem> listarMensagensRecebidas(Usuario usuario) {

        if (usuario == null || usuario.getIdUsuario() == null) {

            return new ArrayList<>();
        }
        return listarMensagens().stream()

                .filter(m -> !m.isConexao())

                .filter(m -> m.getDestinatario() != null
                        && usuario.getIdUsuario().equals(m.getDestinatario().getIdUsuario()))

                .sorted(Comparator.comparing(Mensagem::getDataEnvio,
                        Comparator.nullsLast(Comparator.reverseOrder())))

                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Salva uma nova mensagem no banco.
    public void cadastrarMensagem(Mensagem mensagem) {

        try {

            MensagemDAO.inserirMensagem(mensagem);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao cadastrar mensagem", e);
        }
    }

    // Atualiza os dados de uma mensagem existente.
    public void alterarMensagem(Mensagem mensagem) {

        try {

            MensagemDAO.alterarMensagem(mensagem);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao alterar mensagem", e);
        }
    }

    // Exclui uma mensagem pelo ID.
    public void excluirMensagem(int id) {

        try {

            MensagemDAO.excluirMensagem(id);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao excluir mensagem", e);
        }
    }

    // Retorna os IDs dos usuarios que ja possuem conexao aceita com o usuario informado.
    public Set<Integer> listarConectadosIds(int idUsuario) {

        return listarMensagens().stream()

                .filter(m -> m.isConexao() || "Conexão iniciada".equals(m.getTexto()))

                .filter(m -> m.getRemetente() != null && m.getDestinatario() != null)

                .filter(m -> m.getRemetente().getIdUsuario() == idUsuario
                          || m.getDestinatario().getIdUsuario() == idUsuario)

                .map(m -> m.getRemetente().getIdUsuario() == idUsuario
                        ? m.getDestinatario().getIdUsuario()
                        : m.getRemetente().getIdUsuario())

                .collect(Collectors.toSet());
    }

    // Verifica se ja existe uma solicitacao de conexao pendente entre dois usuarios.
    public Mensagem buscarConexaoPendente(int idRemetente, int idDestinatario) {

        return listarMensagens().stream()

                .filter(m -> "CONEXAO_PENDENTE".equals(m.getTipo()))

                .filter(m -> m.getRemetente() != null && m.getDestinatario() != null)

                .filter(m -> m.getRemetente().getIdUsuario() == idRemetente
                          && m.getDestinatario().getIdUsuario() == idDestinatario)

                .findFirst().orElse(null);
    }

    // Marca uma solicitacao de conexao como aceita.
    public void aceitarConexao(int idMensagem) {

        try {

            MensagemDAO.atualizarTipo(idMensagem, "CONEXAO_ACEITA");
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao aceitar conexão", e);
        }
    }

    // Recusa uma solicitacao de conexao removendo a mensagem pendente.
    public void recusarConexao(int idMensagem) {

        excluirMensagem(idMensagem);
    }
}
