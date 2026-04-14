package com.projeto.oficina.compatibilidade;

public class MemoriaRam {

    private int id_memoriaRam;
    private String modelo;
    private String tipo;
    private int frequencia_mhz;
    private Double preco;

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getId_memoriaRam() {
        return id_memoriaRam;
    }

    public void setId_memoriaRam(int id_memoriaRam) {
        this.id_memoriaRam = id_memoriaRam;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getFrequencia_mhz() {
        return frequencia_mhz;
    }
    
    public void setFrequencia_mhz(int frequencia_mhz) {
        this.frequencia_mhz = frequencia_mhz;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    

}
