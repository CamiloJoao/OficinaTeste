package com.projeto.oficina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Integer idCliente;

    @Column(name = "nome_cliente")
    private String nomeCliente;
    private String telefone;
    @Column(name = "email_cliente")
    private String emailCliente;


    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
=======
    private int id_cliente;

    private String nome_cliente;
    private String telefone;
    private String email_cliente;


    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

<<<<<<< HEAD
    public String getEmailCliente() {
        return emailCliente;
    }
    
    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
=======
    public String getEmail_cliente() {
        return email_cliente;
    }
    
    public void setEmail_cliente(String email_cliente) {
        this.email_cliente = email_cliente;
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
    }

    

}
