package Modelo;

public class SistemaMatch {


    private int idMatch;


    
/**
 *
 * @author Carlos Daniel
 */
    public int getidMatch() {

        return idMatch;

    }



    public void setidMatch(int idMatch) {

        this.idMatch = idMatch;

    }


    public SistemaMatch() {

    }



    public SistemaMatch(int idMatch) {
        
        this.idMatch = idMatch;

    }


    
    public void sugerirParceiros() {

        // Lógica para sugerir parceiros 

    }



    @Override
    public String toString() {

        return "SistemaMatch [idMatch=" + idMatch + "]";

    }



}
