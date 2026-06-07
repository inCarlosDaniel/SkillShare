package com.SkillShare.SkillShare.Modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

// Modelo simples para representar um resultado/registro de match entre usuarios.
public class SistemaMatch {

    @JsonProperty("idMatch")

    private Integer idMatch;

    public Integer getidMatch() {

        return idMatch;
    }

    public void setidMatch(Integer idMatch) {

        this.idMatch = idMatch;
    }

    public SistemaMatch() {
    }

    public SistemaMatch(Integer idMatch) {

        this.idMatch = idMatch;
    }

    // Metodo demonstrativo que indica a acao de sugerir parceiros de estudo.
    public void sugerirParceiros() {

        System.out.println("[SistemaMatch] sugerirParceiros() acionado.");

    }

    @Override
    public String toString() {

        return "SistemaMatch [idMatch=" + idMatch + "]";
    }
}
