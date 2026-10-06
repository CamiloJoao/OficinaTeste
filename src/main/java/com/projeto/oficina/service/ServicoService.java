package com.projeto.oficina.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.model.ServicoPeca;
import com.projeto.oficina.repository.ServicoRepository;
import com.projeto.oficina.repository.ServicoPecaRepository;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ServicoPecaRepository servicoPecaRepository;

    //SALVAR SERVIÇO
    public void salvar(Servico servico) {
        servicoRepository.save(servico);
    }

    //SALVAR PEÇA
    public void salvarPeca(ServicoPeca peca) {
        servicoPecaRepository.save(peca);
    }

    //LISTAR POR TIPO
    public List<Servico> listarPorTipo(TipoServico tipo) {
        return servicoRepository.findByTipoServico(tipo);
    }

    //BUSCAR POR CLIENTE
    public List<Servico> buscarPorCliente(Cliente cliente) {
        return servicoRepository.findByCliente(cliente);
    }

    //LISTAR POR PERIODO (ordenado por data de cadastro e depois por ID)
    public List<Servico> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return servicoRepository.findByDataCadastroBetweenOrderByDataCadastroAscIdServicoAsc(inicio, fim);
    }

    //BUSCAR POR ID
    public Servico buscarPorId(int id) {
        return servicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public List<Servico> listarTodos() {
    return servicoRepository.findAll();
    }

    public List<Servico> buscarUltimos() {
        return servicoRepository.findTop5ByOrderByIdServicoDesc();
    }
}
