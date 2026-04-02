package com.projeto.oficina.compatibilidade;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;

public class PlacaMae {
    private int id_placamae;
    private String modelo;
    private String soquetePlacaMae;
    private String ram_suportada;
    private List<String> processadores_compativeis;
    private Map<String, Integer> interfaces_armazenamento;
    private Boolean video_integrado;

    public Boolean getVideo_integrado() {
        return video_integrado;
    }

    public void setVideo_integrado(Boolean video_integrado) {
        this.video_integrado = video_integrado;
    }

    public int getId_placamae() {
        return id_placamae;
    }

    public void setId_placamae(int id_placamae) {
        this.id_placamae = id_placamae;
    }
    
    public Map<String, Integer> getInterfaces_armazenamento() {
        return interfaces_armazenamento;
    }

    public void setInterfaces_armazenamento(Map<String, Integer> interfaces_armazenamento) {
        this.interfaces_armazenamento = interfaces_armazenamento;
    }

=======

public class PlacaMae {
    private String modelo;
    private String soquete;
    private String ram_suportada;
    private List<String> processadores_compativeis;
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
    private boolean gpu_integrada;


    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

<<<<<<< HEAD
    public String getSoquetePlacaMae() {
        return soquetePlacaMae;
    }

    public void setSoquete(String soquetePlacaMae) {
        this.soquetePlacaMae = soquetePlacaMae;
=======
    public String getSoquete() {
        return soquete;
    }

    public void setSoquete(String soquete) {
        this.soquete = soquete;
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
    }

    public String getRam_suportada() {
        return ram_suportada;
    }

    public void setRam_suportada(String ram_suportada) {
        this.ram_suportada = ram_suportada;
    }

    public List<String> getProcessadores_compativeis() {
        return processadores_compativeis;
    }

    public void setProcessadores_compativeis(List<String> processadores_compativeis) {
        this.processadores_compativeis = processadores_compativeis;
    }

    public boolean isGpu_integrada() {
        return gpu_integrada;
    }

    public void setGpu_integrada(boolean gpu_integrada) {
        this.gpu_integrada = gpu_integrada;
    }

}
