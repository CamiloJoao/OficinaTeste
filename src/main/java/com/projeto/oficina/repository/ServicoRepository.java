package com.projeto.oficina.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {
    List<Servico> findByTipoServico(TipoServico tipoServico);

    List<Servico> findByCliente(Cliente cliente);

    long countByStatus(StatusServico status);

    long countByTipoServico(TipoServico tipo);

    List<Servico> findTop5ByOrderByIdServicoDesc();
}
