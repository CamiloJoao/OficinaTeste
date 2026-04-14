package com.projeto.oficina.service;

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

    //BUSCAR POR ID
    public Servico buscarPorId(int id) {
        return servicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }
}
