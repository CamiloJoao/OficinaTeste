package com.projeto.oficina.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.oficina.enums.TipoUsuario;
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

    // Na edição a senha é opcional: em branco, o hash atual continua; preenchida, é criptografada
    public void atualizar(Usuario usuario, String novaSenha) {
        if (!novaSenha.trim().isEmpty()) {
            usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
        }
        repository.save(usuario);
    }

    public void excluir(Integer id) {
        repository.deleteById(id);
    }

    // true se este usuário é o único administrador do sistema
    // (usado para o sistema nunca ficar sem ninguém que gerencie usuários)
    public boolean ehUltimoAdministrador(Usuario usuario) {
        if (usuario.getTipoUsuario() != TipoUsuario.ADM) {
            return false;
        }

        int administradores = 0;
        for (Usuario u : repository.findAll()) {
            if (u.getTipoUsuario() == TipoUsuario.ADM) {
                administradores++;
            }
        }
        return administradores == 1;
    }
}
