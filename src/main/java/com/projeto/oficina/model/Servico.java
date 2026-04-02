package com.projeto.oficina.model;

import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.enums.StatusServico;
import jakarta.persistence.*;

@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_servico;


    @ManyToOne

    @JoinColumn(name = "idCliente", referencedColumnName = "idCliente")

    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_peca", referencedColumnName = "id_peca")
    private Peca peca;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servico")
    private TipoServico tipo_servico; 

    private String descricao_problema;
    private int orcamento_inicial;
    private int orcamento_final;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusServico status;

    public int getId_servico() {
        return id_servico;
    }

    public void setId_servico(int id_servico) {
        this.id_servico = id_servico;
    }

    public TipoServico getTipo_servico() {
        return tipo_servico;
    }

    public void setTipo_servico(TipoServico tipo_servico) {
        this.tipo_servico = tipo_servico;
    }

    public String getDescricao_problema() {
        return descricao_problema;
    }

    public void setDescricao_problema(String descricao_problema) {
        this.descricao_problema = descricao_problema;
    }

    public int getOrcamento_inicial() {
        return orcamento_inicial;
    }

    public void setOrcamento_inicial(int orcamento_inicial) {
        this.orcamento_inicial = orcamento_inicial;
    }

    public int getOrcamento_final() {
        return orcamento_final;
    }

    public void setOrcamento_final(int orcamento_final) {
        this.orcamento_final = orcamento_final;
    }

    public StatusServico getStatus() {
        return status;
    }
    
    public void setStatus(StatusServico status) {
        this.status = status;
    }

    







}
