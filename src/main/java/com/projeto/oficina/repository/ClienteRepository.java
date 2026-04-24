package com.projeto.oficina.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.oficina.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNomeClienteContainingIgnoreCase(String nome);
    Cliente findByEmailCliente(String emailCliente);

   

}
