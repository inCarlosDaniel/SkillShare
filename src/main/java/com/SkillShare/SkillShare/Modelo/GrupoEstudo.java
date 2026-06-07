package com.SkillShare.SkillShare.Modelo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

// Representa um grupo de estudo criado por um usuario e formado por membros com interesse em uma materia.
public class GrupoEstudo {

    @JsonProperty("idGrupo")

    private Integer idGrupo;

    private String nome;

    private String materia;

    private String exameAlvo;

    // O criador tem permissoes especiais, como excluir o grupo ou administrar conteudos.
    private Usuario criador;

    // Lista de usuarios que participam do grupo e podem acessar o mural interno.
    private ArrayList<Usuario> membros;

    public GrupoEstudo() {}

    public GrupoEstudo(Integer idGrupo, String nome, String materia, String exameAlvo,
                       Usuario criador, ArrayList<Usuario> membros) {

        this.idGrupo = idGrupo;

        this.nome = nome;

        this.materia = materia;

        this.exameAlvo = exameAlvo;

        this.criador = criador;

        this.membros = membros;
    }

    public Integer getidGrupo() { return idGrupo; }
    
    public void setidGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getMateria() { return materia; }

    public void setMateria(String materia) { this.materia = materia; }

    public String getExameAlvo() { return exameAlvo; }

    public void setExameAlvo(String exameAlvo) { this.exameAlvo = exameAlvo; }

    public Usuario getCriador() { return criador; }

    public void setCriador(Usuario criador) { this.criador = criador; }

    public ArrayList<Usuario> getMembros() { return membros; }

    public void setMembros(ArrayList<Usuario> membros) { this.membros = membros; }

    @Override
    public String toString() {

        return "GrupoEstudo[idGrupo=" + idGrupo + ", nome=" + nome + ", materia=" + materia
                + ", exameAlvo=" + exameAlvo + ", criador=" + criador
                + ", membros=" + (membros != null ? membros.size() : 0) + "]";

    }
}
