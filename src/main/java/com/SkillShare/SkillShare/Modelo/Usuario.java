package com.SkillShare.SkillShare.Modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;

// Representa uma pessoa cadastrada no SkillShare, com dados de login e perfil de aprendizado.
public class Usuario {

    @JsonProperty("idUsuario")

    private Integer idUsuario;

    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    // A senha pode entrar em requisicoes, mas nao deve sair nas respostas JSON.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    @NotBlank
    @Size(min = 6)

    private String senha;

    // Campos usados para montar perfil publico, sugestoes de match e recomendacoes de estudo.
    private String habilidades;

    private String interesses;

    private String dificuldades;

    public Usuario() {}

    public Usuario(Integer idUsuario, String nome, String email, String senha,
                   String habilidades, String interesses, String dificuldades) {

        this.idUsuario = idUsuario;

        this.nome = nome;

        this.email = email;

        this.senha = senha;

        this.habilidades = habilidades;

        this.interesses = interesses;

        this.dificuldades = dificuldades;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getHabilidades() { return habilidades; }
    public void setHabilidades(String habilidades) { this.habilidades = habilidades; }

    public String getInteresses() { return interesses; }
    public void setInteresses(String interesses) { this.interesses = interesses; }

    public String getDificuldades() { return dificuldades; }
    public void setDificuldades(String dificuldades) { this.dificuldades = dificuldades; }

    @Override
    public String toString() {

        return "Usuario{idUsuario=" + idUsuario + ", nome=" + nome + ", email=" + email
                + ", senha=[PROTEGIDA], habilidades=" + habilidades
                + ", interesses=" + interesses + ", dificuldades=" + dificuldades + "}";
    }
}
