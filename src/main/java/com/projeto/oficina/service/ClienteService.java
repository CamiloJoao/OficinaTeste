package com.projeto.oficina.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente buscarPorEmail(String email) {
    return repository.findByEmailCliente(email);
    }
    

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return repository.findByNomeClienteIgnoreCase(nome);
    }

    public void salvar(Cliente cliente) {
        repository.save(cliente);
    }

    public void excluir(Integer id) {
        repository.deleteById(id);
    }

    public Cliente buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

}