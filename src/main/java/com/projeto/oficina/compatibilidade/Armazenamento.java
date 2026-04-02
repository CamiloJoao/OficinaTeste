package com.projeto.oficina.compatibilidade;

public class Armazenamento {


    private int id_armazenamento;
    private String modelo;
    private String tipo; //hd ou ssd
    private String interfaceConexao; //Sata ou M.2
    private int capacidadeGB;


    public int getId_armazenamento() {
        return id_armazenamento;
    }
    public void setId_armazenamento(int id_armazenamento) {
        this.id_armazenamento = id_armazenamento;
    }
    
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getInterfaceConexao() {
        return interfaceConexao;
    }
    public void setInterfaceConexao(String interfaceConexao) {
        this.interfaceConexao = interfaceConexao;
    }
    public int getCapacidadeGB() {
        return capacidadeGB;
    }
    public void setCapacidadeGB(int capacidadeGB) {
        this.capacidadeGB = capacidadeGB;
    }

}
