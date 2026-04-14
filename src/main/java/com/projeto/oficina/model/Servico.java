package com.projeto.oficina.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;

@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServico;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servico")
    private TipoServico tipoServico;

    @Column(name = "descricao_problema")
    private String descricaoProblema;

    @Column(name = "pecas_cadastradas")
    private String pecasCadastradas;

    @Column(name = "orcamento_inicial")
    private Double orcamentoInicial;

    @Column(name = "orcamento_final")
    private Double orcamentoFinal;

    @Enumerated(EnumType.STRING)
    private StatusServico status;

    @Column(name = "data_prevista_conclusao")
    private LocalDate dataPrevistaConclusao;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoPeca> pecas = new ArrayList<>();

    public List<ServicoPeca> getPecas() {
        return pecas;
    }

    public void setPecas(List<ServicoPeca> pecas) {
        this.pecas = pecas;
    }

    public void adicionarPeca(ServicoPeca peca) {
        pecas.add(peca);
        peca.setServico(this);
    }

    public void removerPeca(ServicoPeca peca) {
        pecas.remove(peca);
        peca.setServico(null);
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public String getPecasCadastradas() {
        return pecasCadastradas;
    }

    public void setPecasCadastradas(String pecasCadastradas) {
        this.pecasCadastradas = pecasCadastradas;
    }

    public Double getOrcamentoInicial() {
        return orcamentoInicial;
    }

    public void setOrcamentoInicial(Double orcamentoInicial) {
        this.orcamentoInicial = orcamentoInicial;
    }

     public Double getOrcamentoFinal() {
        return orcamentoFinal;
    }

    public void setOrcamentoFinal(Double orcamentoFinal) {
        this.orcamentoFinal = orcamentoFinal;
    }

    public StatusServico getStatus() {
        return status;
    }

    public void setStatus(StatusServico status) {
        this.status = status;
    }

    public LocalDate getDataPrevistaConclusao() {
        return dataPrevistaConclusao;
    }

    public void setDataPrevistaConclusao(LocalDate dataPrevistaConclusao) {
        this.dataPrevistaConclusao = dataPrevistaConclusao;
    }

    
}

