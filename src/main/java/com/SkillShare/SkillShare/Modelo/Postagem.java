package com.SkillShare.SkillShare.Modelo;

import java.time.LocalDateTime;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

// Representa uma publicacao do feed ou do mural de grupo, com autor, texto, anexos e votos.
public class Postagem {

    @JsonProperty("idPostagem")

    private Integer idPostagem;

    private String conteudo;

    // Usuario que criou a postagem.
    private Usuario autor;

    private LocalDateTime dataPublicacao;

    // Arquivos ligados a postagem, como imagens ou documentos.
    private ArrayList<Anexo> anexos;

    // Tipo da postagem; permite diferenciar publicacoes comuns, quizzes e conteudos ligados a grupos.
    private String tipo = "POST";

    // ID usado para relacionar a postagem a outro recurso, como um grupo ou material.
    private Integer idReferencia;

    private int upvoteCount;

    public Postagem() {}

    public Postagem(Usuario autor, String conteudo, LocalDateTime dataPublicacao,
                    Integer idPostagem, ArrayList<Anexo> anexos) {

        this.autor = autor;

        this.conteudo = conteudo;

        this.dataPublicacao = dataPublicacao;

        this.idPostagem = idPostagem;

        this.anexos = anexos;

        this.tipo = "POST";
    }

    public Integer getidPostagem() { return idPostagem; }

    public void setidPostagem(Integer idPostagem) { this.idPostagem = idPostagem; }


    public String getConteudo() { return conteudo; }

    public void setConteudo(String conteudo) { this.conteudo = conteudo; }


    public LocalDateTime getDataPublicacao() { return dataPublicacao; }

    public void setDataPublicacao(LocalDateTime dataPublicacao) { this.dataPublicacao = dataPublicacao; }

    public Usuario getAutor() { return autor; }

    public void setAutor(Usuario autor) { this.autor = autor; }

    public ArrayList<Anexo> getAnexos() { return anexos; }

    public void setAnexos(ArrayList<Anexo> anexos) { this.anexos = anexos; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo != null ? tipo : "POST"; }

    public Integer getIdReferencia() { return idReferencia; }

    public void setIdReferencia(Integer idReferencia) { this.idReferencia = idReferencia; }

    public int getUpvoteCount() { return upvoteCount; }

    public void setUpvoteCount(int upvoteCount) { this.upvoteCount = upvoteCount; }

    @Override
    public String toString() {

        return "Postagem[idPostagem=" + idPostagem + ", conteudo=" + conteudo

                + ", dataPublicacao=" + dataPublicacao

                + ", autor=" + (autor != null ? autor.getNome() : "null")

                + ", anexos=" + (anexos != null ? anexos.size() : 0) + "]";

    }
}
