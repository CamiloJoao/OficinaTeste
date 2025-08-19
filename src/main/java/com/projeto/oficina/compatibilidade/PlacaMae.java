package com.projeto.oficina.compatibilidade;

import java.util.List;

public class PlacaMae {
    private String modelo;
    private String soquete;
    private String ram_suportada;
    private List<String> processadores_compativeis;
    private boolean gpu_integrada;


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
