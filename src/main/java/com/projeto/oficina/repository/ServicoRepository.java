package com.projeto.oficina.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {
    List<Servico> findByCliente(Cliente cliente);

    List<Servico> findByDataCadastroBetweenOrderByDataCadastroAscIdServicoAsc(LocalDate inicio, LocalDate fim);

    List<Servico> findTop5ByOrderByIdServicoDesc();
}
