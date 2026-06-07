package com.SkillShare.SkillShare.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.SkillShare.SkillShare.Modelo.Postagem;
import com.SkillShare.SkillShare.Modelo.Usuario;
import com.SkillShare.SkillShare.Persistencia.PostagemDAO;

@Service
public class PostagemService {

    // Operacoes principais de cadastro, busca, alteracao e remocao de postagens.

    // Lista todas as postagens cadastradas no banco.
    public ArrayList<Postagem> listarPostagens() {

        try {

            return PostagemDAO.listarPostagens();
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar postagens", e);
        }
    }

    // Salva uma nova postagem no banco.
    public void cadastrarPostagem(Postagem postagem) {

        try {

            PostagemDAO.inserirPostagem(postagem);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao cadastrar postagem", e);
        }
    }

    // Atualiza os dados de uma postagem existente.
    public void alterarPostagem(Postagem postagem) {

        try {

            PostagemDAO.alterarPostagem(postagem);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao alterar postagem", e);
        }
    }

    // Busca uma postagem especifica pelo ID.
    public Postagem buscarPostagemPorId(int id) {

        try {

            return PostagemDAO.buscarPostagemPorId(id);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar postagem", e);
        }
    }

    // Lista todas as postagens feitas por um usuario.
    public ArrayList<Postagem> listarPostagensPorUsuario(int idUsuario) {

        try {

            return PostagemDAO.listarPostagensPorUsuario(idUsuario);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar postagens do usuario", e);
        }
    }

    // Lista todas as postagens ligadas a um grupo de estudo.
    public ArrayList<Postagem> listarPostagensPorGrupo(int idGrupo) {

        try {

            return PostagemDAO.listarPostagensPorGrupo(idGrupo);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar postagens do grupo", e);
        }
    }

    // Remove uma postagem do banco pelo ID.
    public void excluirPostagem(int id) {

        try {

            PostagemDAO.excluirPostagem(id);
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao excluir postagem", e);
        }
    }

    // Operacoes de upvote: registram curtidas como postagens especiais do tipo UPVOTE.

    // Cria um upvote para a postagem, evitando voto duplicado do mesmo usuario.
    public void upvotar(int idPostagem, Usuario usuario) {

        if (usuarioUpvotou(idPostagem, usuario.getIdUsuario())) return;

        Postagem upvote = new Postagem();

        upvote.setConteudo(null);

        upvote.setTipo("UPVOTE");

        upvote.setIdReferencia(idPostagem);

        upvote.setAutor(usuario);

        upvote.setDataPublicacao(LocalDateTime.now());

        cadastrarPostagem(upvote);

        try {

            PostagemDAO.incrementarUpvote(idPostagem);

        } catch (java.sql.SQLException e) {

            throw new RuntimeException("Erro ao incrementar upvote", e);
        }
    }

    // Remove o upvote do usuario e diminui o contador da postagem original.
    public void removerUpvote(int idPostagem, int idUsuario) {

        try {

            PostagemDAO.buscarUpvotes(idPostagem).stream()

                    .filter(p -> p.getAutor() != null && Integer.valueOf(idUsuario).equals(p.getAutor().getIdUsuario()))

                    .forEach(p -> excluirPostagem(p.getidPostagem()));

            PostagemDAO.decrementarUpvote(idPostagem);

        } catch (java.sql.SQLException e) {

            throw new RuntimeException("Erro ao remover upvote", e);
        }
    }

    // Conta quantos upvotes uma postagem recebeu.
    public long contarUpvotes(int idPostagem) {

        try {

            return PostagemDAO.buscarUpvotes(idPostagem).size();

        } catch (java.sql.SQLException e) {

            throw new RuntimeException("Erro ao contar upvotes", e);
        }
    }

    // Verifica se um usuario especifico ja deu upvote na postagem.
    public boolean usuarioUpvotou(int idPostagem, Integer idUsuario) {

        if (idUsuario == null) return false;

        try {

            return PostagemDAO.buscarUpvotes(idPostagem).stream()

                    .anyMatch(p -> p.getAutor() != null && idUsuario.equals(p.getAutor().getIdUsuario()));

        } catch (java.sql.SQLException e) {

            return false;
        }
    }

    // Operacoes de comentario: comentarios tambem sao salvos como postagens especiais.

    // Cria um comentario ligado a uma postagem original.
    public void comentar(int idPostagem, String texto, Usuario autor) {

        Postagem comentario = new Postagem();

        comentario.setConteudo(texto);

        comentario.setTipo("COMENTARIO");

        comentario.setIdReferencia(idPostagem);

        comentario.setAutor(autor);

        comentario.setDataPublicacao(LocalDateTime.now());

        cadastrarPostagem(comentario);
    }

    // Lista os comentarios de uma postagem.
    public List<Postagem> listarComentarios(int idPostagem) {

        try {

            return PostagemDAO.buscarComentarios(idPostagem);

        } catch (java.sql.SQLException e) {

            throw new RuntimeException("Erro ao listar comentarios", e);
        }
    }

}
