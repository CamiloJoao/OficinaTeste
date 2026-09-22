package com.projeto.oficina.compatibilidade;

public class PlacadeVideo {

    private int id_placadevideo;
    private String modelo;
    private String interfacePcie;
    private Integer consumoWatts;
    private Integer memoriaGB;
    private Double preco;

    public int getId_placadevideo() {
        return id_placadevideo;
    }

    public void setId_placadevideo(int id_placadevideo) {
        this.id_placadevideo = id_placadevideo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getInterfacePcie() {
        return interfacePcie;
    }

    public void setInterfacePcie(String interfacePcie) {
        this.interfacePcie = interfacePcie;
    }

    public Integer getConsumoWatts() {
        return consumoWatts;
    }

    public void setConsumoWatts(Integer consumoWatts) {
        this.consumoWatts = consumoWatts;
    }

    public Integer getMemoriaGB() {
        return memoriaGB;
    }

    public void setMemoriaGB(Integer memoriaGB) {
        this.memoriaGB = memoriaGB;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
