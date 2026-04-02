package com.projeto.oficina.compatibilidade;

public class Processador {
<<<<<<< HEAD

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
=======
    private String modelo;
    private String soquete;
    private boolean possui_video_integrado;

>>>>>>> 87ea526fe2591ec426152c378be484796860f804

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
    }

    

}
