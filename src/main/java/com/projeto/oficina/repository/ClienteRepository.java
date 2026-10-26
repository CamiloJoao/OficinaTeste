package com.projeto.oficina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.oficina.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Cliente findByEmailCliente(String emailCliente);
}
