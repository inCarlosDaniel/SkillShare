package Modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Carlos Daniel
 */
public class Postagem {


 private int idPostagem;


 private String conteudo;


 private Usuario autor;


 private LocalDateTime dataPublicacao;


 private ArrayList<Anexo> anexos;


    public int getidPostagem() {

        return idPostagem;

    }



    public void setidPostagem(int idPostagem) {

        this.idPostagem = idPostagem;

    }



    public String getConteudo() {

        return conteudo;

    }



    public void setConteudo(String conteudo) {

        this.conteudo = conteudo;

    }



    public LocalDateTime getDataPublicacao() {

        return dataPublicacao;

    }



    public void setDataPublicacao(LocalDateTime dataPublicacao) {

        this.dataPublicacao = dataPublicacao;

    }



    public Usuario getAutor() {

        return autor;

    }



    public void setAutor(Usuario autor) {

        this.autor = autor;

    }



    public ArrayList<Anexo> getAnexos() {

        return anexos;

    }


    public void setAnexos(ArrayList<Anexo> anexos) {

        this.anexos = anexos;

    }


    public Postagem(){


        
    }



    public Postagem(Usuario autor, String conteudo, LocalDateTime dataPublicacao, int idPostagem, ArrayList<Anexo> anexos) {


        this.autor = autor;


        this.conteudo = conteudo;


        this.dataPublicacao = dataPublicacao;


        this.idPostagem = idPostagem;

        this.anexos = anexos;


    }

    

    public void publicar() {

        // Lógica para publicar a postagem

    }



    public void excluir() {

        // Lógica para excluir a postagem

    }



    public void receberUpvote() {

        // Lógica para receber um upvote

    }



    @Override
    public String toString() {

        return "Postagem [idPostagem=" + idPostagem + ", conteudo=" + conteudo + ", dataPublicacao=" + dataPublicacao
                + ", autor=" + autor.getNome() + ", quantidadeAnexos=" + anexos.size() + "]";

    }



    

}
