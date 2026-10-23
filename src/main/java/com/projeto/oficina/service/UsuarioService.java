package com.projeto.oficina.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.oficina.model.Usuario;
import com.projeto.oficina.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Ordenado pelo ID para a tabela não trocar de ordem
    public List<Usuario> listarTodos() {
        return repository.findAll(Sort.by("idUsuario"));
    }

    public Usuario buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmailUsuario(email);
    }

    // Criptografa a senha com BCrypt antes de gravar: o banco nunca guarda a senha em texto
    public void salvar(Usuario usuario) {
        usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        repository.save(usuario);
    }
}
