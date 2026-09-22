package com.projeto.oficina.compatibilidade;

public class Fonte {

    private int id_fonte;
    private String modelo;
    private Integer potenciaWatts;
    private Double preco;

    public int getId_fonte() {
        return id_fonte;
    }

    public void setId_fonte(int id_fonte) {
        this.id_fonte = id_fonte;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getPotenciaWatts() {
        return potenciaWatts;
    }

    public void setPotenciaWatts(Integer potenciaWatts) {
        this.potenciaWatts = potenciaWatts;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
