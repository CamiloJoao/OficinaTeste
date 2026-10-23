package com.projeto.oficina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.oficina.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmailUsuario(String emailUsuario);
}
