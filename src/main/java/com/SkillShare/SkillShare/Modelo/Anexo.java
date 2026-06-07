package com.SkillShare.SkillShare.Modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

// Representa um arquivo anexado a uma postagem, como imagem, PDF ou outro material enviado pelo usuario.
public class Anexo {

    @JsonProperty("idAnexo")

    private Integer idAnexo;

    // Dados basicos usados para identificar e acessar o arquivo anexado.
    private String nomeArquivo;

    private String tipo;

    private String url;

    // Postagem dona do anexo; liga o arquivo ao conteudo onde ele foi publicado.
    private Postagem postagem;

    public Anexo() {}

    public Anexo(Integer idAnexo, String nomeArquivo, String tipo, String url) {

        this.idAnexo = idAnexo;

        this.nomeArquivo = nomeArquivo;

        this.tipo = tipo;

        this.url = url;
    }

    public Integer getidAnexo() { return idAnexo; }
    public void setidAnexo(Integer idAnexo) { this.idAnexo = idAnexo; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Postagem getPostagem() { return postagem; }
    public void setPostagem(Postagem postagem) { this.postagem = postagem; }

    @Override
    public String toString() {

        return "Anexo{idAnexo=" + idAnexo + ", nomeArquivo=" + nomeArquivo
                + ", tipo=" + tipo + ", url=" + url + "}";
    }
}
