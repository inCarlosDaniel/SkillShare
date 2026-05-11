
package Modelo;

import java.time.LocalDateTime;

/**
 *
 * @author Carlos Daniel
 */
public class Mensagem {



    private int idMensagem;


    private String texto;


    private LocalDateTime dataEnvio;


    private boolean lida;


    private Usuario remetente;


    private Usuario destinatario;



    public int getidMensagem() {

        return idMensagem;

    }



    public void setidMensagem(int idMensagem) {

        this.idMensagem = idMensagem;

    }



    public String getTexto() {

        return texto;

    }



    public void setTexto(String texto) {

        this.texto = texto;

    }



    public LocalDateTime getDataEnvio() {

        return dataEnvio;

    }



    public void setDataEnvio(LocalDateTime dataEnvio) {

        this.dataEnvio = dataEnvio;

    }



    public boolean isLida() {

        return lida;

    }



    public void setLida(boolean lida) {

        this.lida = lida;

    }



    public Usuario getRemetente() {

        return remetente;

    }



    public void setRemetente(Usuario remetente) {

        this.remetente = remetente;

    }



    public Usuario getDestinatario() {

        return destinatario;

    }
    


    public void setDestinatario(Usuario destinatario) {

        this.destinatario = destinatario;

    }


    public Mensagem(){


        
    }



    public Mensagem(LocalDateTime dataEnvio, Usuario destinatario, int idMensagem, boolean lida, Usuario remetente, String texto) {


        this.dataEnvio = dataEnvio;


        this.destinatario = destinatario;


        this.idMensagem = idMensagem;


        this.lida = lida;


        this.remetente = remetente;


        this.texto = texto;


    }



    

public void enviar() {

        // Lógica para enviar a mensagem

    }



    public void excluir() {

        // Lógica para excluir a mensagem

    }



    public void validarConteudo() {

        // Lógica para validar o conteúdo da mensagem

    }



    @Override
    public String toString() {

        return "Mensagem [idMensagem=" + idMensagem + ", texto=" + texto + ", dataEnvio=" + dataEnvio + ", lida=" + lida
                + ", remetente=" + remetente.getNome() + ", destinatario=" + destinatario.getNome() + "]";

    }


    

    
}
