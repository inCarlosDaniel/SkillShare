package com.SkillShare.SkillShare.Modelo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

// Representa uma conversa entre dois usuarios ou um pedido de conexao dentro do chat.
public class Mensagem {

    @JsonProperty("idMensagem")

    private Integer idMensagem;

    private String texto;

    private LocalDateTime dataEnvio;

    private boolean lida;

    // Usuarios envolvidos na mensagem: quem envia e quem recebe.
    private Usuario remetente;

    private Usuario destinatario;

    // Diferencia mensagens comuns de estados de conexao, como pedido pendente ou aceito.
    private String tipo = "MENSAGEM";

    public Mensagem() {}

    public Mensagem(LocalDateTime dataEnvio, Usuario destinatario, Integer idMensagem,
                    boolean lida, Usuario remetente, String texto) {

        this.dataEnvio = dataEnvio;

        this.destinatario = destinatario;

        this.idMensagem = idMensagem;

        this.lida = lida;

        this.remetente = remetente;

        this.texto = texto;

        this.tipo = "MENSAGEM";
    }

    public Integer getIdMensagem() { return idMensagem; }
    public void setIdMensagem(Integer idMensagem) { this.idMensagem = idMensagem; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public Usuario getRemetente() { return remetente; }
    public void setRemetente(Usuario remetente) { this.remetente = remetente; }

    public Usuario getDestinatario() { return destinatario; }
    public void setDestinatario(Usuario destinatario) { this.destinatario = destinatario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo != null ? tipo : "MENSAGEM"; }

    // Indica se este registro representa uma conexao em vez de uma mensagem comum.
    public boolean isConexao() { return "CONEXAO_PENDENTE".equals(tipo) || "CONEXAO_ACEITA".equals(tipo); }

    @Override
    public String toString() {

        return "Mensagem[idMensagem=" + idMensagem + ", texto=" + texto
                + ", dataEnvio=" + dataEnvio + ", lida=" + lida
                + ", remetente=" + (remetente != null ? remetente.getNome() : "null")

                + ", destinatario=" + (destinatario != null ? destinatario.getNome() : "null") + "]";
    }
}
