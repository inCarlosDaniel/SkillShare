package com.SkillShare.SkillShare.Modelo;



// Modelo simples que associa uma instancia da aplicacao a um usuario.
public class SkillShare {


    private Usuario usuario;


    public Usuario getUsuario() {

        return usuario;

    }



    public void setUsuario(Usuario usuario) {

        this.usuario = usuario;

    }


    public SkillShare() {

    }



    public SkillShare(Usuario usuario) {

        this.usuario = usuario;

    }



    // Metodo demonstrativo para representar a acao de cadastro.
    public void Cadastro() {

        System.out.println("Usuário cadastrado com sucesso!");

    }



    // Metodo demonstrativo para representar a acao de login.
    public void Login() {

        System.out.println("Login realizado com sucesso!");

    }



    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("SkillShare{");

        sb.append("usuario=").append(usuario != null ? usuario.getNome() : "null");

        sb.append('}');

        return sb.toString();

    }




}
