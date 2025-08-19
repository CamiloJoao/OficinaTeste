package com.projeto.oficina.compatibilidade;

public class Processador {
    private String modelo;
    private String soquete;
    private boolean possui_video_integrado;


    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSoquete() {
        return soquete;
    }

    public void setSoquete(String soquete) {
        this.soquete = soquete;
    }

    public boolean isPossui_video_integrado() {
        return possui_video_integrado;
    }
    
    public void setPossui_video_integrado(boolean possui_video_integrado) {
        this.possui_video_integrado = possui_video_integrado;
    }

    

}
