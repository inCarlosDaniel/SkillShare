package com.SkillShare.SkillShare.Modelo;

import java.sql.Blob;

import com.fasterxml.jackson.annotation.JsonProperty;

// Representa um material de estudo enviado ao repositorio, como PDF, imagem ou outro arquivo.
public class Material {

    @JsonProperty("idMaterial")

    private Integer idMaterial;

    private String titulo;

    private String categoria;

    // Conteudo binario do arquivo salvo no banco de dados.
    private Blob arquivo;

    // Usuario que enviou o material e pode ter permissao para editar ou excluir.
    private Usuario autor;

    private String url;

    public Material() {}

    public Material(Blob arquivo, Usuario autor, String categoria, Integer idMaterial, String titulo) {

        this.arquivo = arquivo;

        this.autor = autor;

        this.categoria = categoria;

        this.idMaterial = idMaterial;

        this.titulo = titulo;
    }

    public Integer getIdMaterial() { return idMaterial; }
    public void setIdMaterial(Integer idMaterial) { this.idMaterial = idMaterial; }

    public Integer getidMaterial() { return idMaterial; }
    public void setidMaterial(Integer idMaterial) { this.idMaterial = idMaterial; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Blob getArquivo() { return arquivo; }
    public void setArquivo(Blob arquivo) { this.arquivo = arquivo; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {

        return "Material{idMaterial=" + idMaterial + ", titulo=" + titulo
                + ", categoria=" + categoria
                + ", autor=" + (autor != null ? autor.getNome() : "null") + "}";
    }
}
