package com.projeto.oficina.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.repository.ServicoRepository;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    public void salvar(Servico servico) {
        servicoRepository.save(servico);
    }

    public List<Servico> buscarPorCliente(Cliente cliente) {
        return servicoRepository.findByCliente(cliente);
    }

    public List<Servico> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return servicoRepository.findByDataCadastroBetweenOrderByDataCadastroAscIdServicoAsc(inicio, fim);
    }

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
