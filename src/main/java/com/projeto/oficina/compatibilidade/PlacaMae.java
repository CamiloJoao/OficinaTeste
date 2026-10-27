package com.projeto.oficina.compatibilidade;

import java.util.Map;

public class PlacaMae {

    private int id_placamae;
    private String modelo;
    private String soquetePlacaMae;
    private String ram_suportada;
    private Map<String, Integer> interfaces_armazenamento;
    private boolean video_integrado;
    private Double preco;
    private String interfacePcie;

    public int getId_placamae() {
        return id_placamae;
    }

    public void setId_placamae(int id_placamae) {
        this.id_placamae = id_placamae;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSoquetePlacaMae() {
        return soquetePlacaMae;
    }

    public void setSoquetePlacaMae(String soquetePlacaMae) {
        this.soquetePlacaMae = soquetePlacaMae;
    }

    public String getRam_suportada() {
        return ram_suportada;
    }

    public void setRam_suportada(String ram_suportada) {
        this.ram_suportada = ram_suportada;
    }

    public Map<String, Integer> getInterfaces_armazenamento() {
        return interfaces_armazenamento;
    }

    public void setInterfaces_armazenamento(Map<String, Integer> interfaces_armazenamento) {
        this.interfaces_armazenamento = interfaces_armazenamento;
    }

    public boolean isVideo_integrado() {
        return video_integrado;
    }

    public void setVideo_integrado(boolean video_integrado) {
        this.video_integrado = video_integrado;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getInterfacePcie() {
        return interfacePcie;
    }

    public void setInterfacePcie(String interfacePcie) {
        this.interfacePcie = interfacePcie;
    }
}
