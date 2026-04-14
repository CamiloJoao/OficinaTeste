package com.projeto.oficina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.oficina.model.ServicoPeca;

public interface ServicoPecaRepository extends JpaRepository<ServicoPeca, Integer> {
}