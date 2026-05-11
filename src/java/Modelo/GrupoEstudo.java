package Modelo;

import java.util.ArrayList;

/**
 *
 * @author Carlos Daniel
 */
public class GrupoEstudo {


    private int idGrupo;


    private String nome, materia, exameAlvo;


    private Usuario criador;


    private ArrayList<Usuario> membros;

   

    public int getidGrupo() {

        return idGrupo;

    }



    public void setidGrupo(int idGrupo) {


        this.idGrupo = idGrupo;


    }



    public String getNome() {

        return nome;

    }



    public void setNome(String nome) {

        this.nome = nome;

    }



    public String getMateria() {

        return materia;

    }



    public void setMateria(String materia) {

        this.materia = materia;

    }



    public String getExameAlvo() {

        return exameAlvo;

    }



    public void setExameAlvo(String exameAlvo) {

        this.exameAlvo = exameAlvo;

    }



    public Usuario getCriador() {

        return criador;

    }



    public void setCriador(Usuario criador) {

        this.criador = criador;

    }



    public ArrayList<Usuario> getMembros() {

        return membros;

    }



    public void setMembros(ArrayList<Usuario> membros) {

        this.membros = membros;

    }


     public GrupoEstudo() {

    }



    public GrupoEstudo(int idGrupo, String nome, String materia, String exameAlvo, Usuario criador, ArrayList<Usuario> membros) {


        this.idGrupo = idGrupo;


        this.nome = nome;


        this.materia = materia;


        this.exameAlvo = exameAlvo;


        this.criador = criador;


        this.membros = membros;


    }

    public void criarGrupo() {

        // Lógica para criar um grupo de estudo

    }



    @Override
    public String toString() {
        return "GrupoEstudo [idGrupo=" + idGrupo + ", nome=" + nome + ", materia=" + materia + ", exameAlvo="
                + exameAlvo + ", criador=" + criador + ", quantidadesMembros=" + membros.size() + "]";
    }


    
}
