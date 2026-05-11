package Modelo;

import java.sql.Blob;

/**
 *
 * @author Carlos Daniel
 */
public class Material {


    private int idMaterial;


    private String titulo, categoria;


    private Blob arquivo;


    private Usuario autor;




    public int getidMaterial() {

        return idMaterial;

    }



    public void setidMaterial(int idMaterial) {

        this.idMaterial = idMaterial;

    }



    public String getTitulo() {

        return titulo;

    }



    public void setTitulo(String titulo) {

        this.titulo = titulo;

    }



    public String getCategoria() {

        return categoria;

    }



    public void setCategoria(String categoria) {

        this.categoria = categoria;

    }



    public Blob getArquivo() {

        return arquivo;


    }



    public void setArquivo(Blob arquivo) {

        this.arquivo = arquivo;

    }



    public Usuario getAutor() {

        return autor;

    }



    public void setAutor(Usuario autor) {

        this.autor = autor;

    }

    public Material(){

        
    }

    public Material(Blob arquivo, Usuario autor, String categoria, int idMaterial, String titulo) {


        this.arquivo = arquivo;


        this.autor = autor;


        this.categoria = categoria;


        this.idMaterial = idMaterial;


        this.titulo = titulo;


    }

    public void upload() {

        // Lógica para upload do material

    }

    public void download() {

        // Lógica para download do material

    }

    public void avaliar() {

        // Lógica para avaliação do material

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Material{");

        sb.append("idMaterial=").append(idMaterial);

        sb.append(", titulo=").append(titulo);

        sb.append(", categoria=").append(categoria);

        sb.append(", arquivo=").append(arquivo);

        sb.append(", autor=").append(autor.getNome());

        sb.append('}');

        return sb.toString();

    }




    

}
