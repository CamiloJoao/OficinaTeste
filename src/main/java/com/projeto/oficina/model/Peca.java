package com.projeto.oficina.model;

import com.projeto.oficina.enums.TipoPeca;
import jakarta.persistence.*;

@Entity
@Table(name = "peca")
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_peca;

    private String nome_peca;

    @Enumerated(EnumType.STRING)
    @Column (name = "tipo_peca")
    private TipoPeca tipo_peca; 

    private float preco;
    
    
    public int getId_peca() {
        return id_peca;
    }

    public void setId_peca(int id_peca) {
        this.id_peca = id_peca;
    }

    public String getNome_peca() {
        return nome_peca;
    }

    public void setNome_peca(String nome_peca) {
        this.nome_peca = nome_peca;
    }

    public TipoPeca getTipo_peca() {
        return tipo_peca;
    }

    public void setTipo_peca(TipoPeca tipo_peca) {
        this.tipo_peca = tipo_peca;
    }

    public float getPreco() {
        return preco;
    }
    
    public void setPreco(float preco) {
        this.preco = preco;
    }

    

    

}
