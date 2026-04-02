package com.projeto.oficina.compatibilidade;

public class Processador {

    private int id_processador;
    private String modelo;
    private String soqueteProcessador;
    private boolean video_integrado;

     public int getId_processador() {
        return id_processador;
    }

    public void setId_processador(int id_processador) {
        this.id_processador = id_processador;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSoqueteProcessador() {
        return soqueteProcessador;
    }

    public void setSoqueteProcessador(String soqueteProcessador) {
        this.soqueteProcessador = soqueteProcessador;
    }

    public boolean isvideo_integrado() {
        return video_integrado;
    }
    
    public void setvideo_integrado(boolean video_integrado) {
        this.video_integrado = video_integrado;
    }

    

}
