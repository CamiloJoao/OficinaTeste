package com.projeto.oficina.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.oficina.model.ServicoPeca;
import com.projeto.oficina.repository.ServicoPecaRepository;

@Service
public class ServicoPecaService {

    @Autowired
    private ServicoPecaRepository repository;

    public void salvar(ServicoPeca peca) {
        repository.save(peca);
    }
}
