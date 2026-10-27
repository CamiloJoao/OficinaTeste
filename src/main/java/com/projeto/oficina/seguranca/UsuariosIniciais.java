package com.projeto.oficina.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.projeto.oficina.enums.TipoUsuario;
import com.projeto.oficina.model.Usuario;
import com.projeto.oficina.repository.UsuarioRepository;

// Cria o administrador e o funcionário quando o sistema sobe com a tabela usuario vazia
// (primeira execução ou banco recriado)
@Component
public class UsuariosIniciais implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${oficina.admin.email}")
    private String emailAdmin;

    @Value("${oficina.admin.senha-hash}")
    private String senhaHashAdmin;

    @Value("${oficina.funcionario.email}")
    private String emailFuncionario;

    @Value("${oficina.funcionario.senha-hash}")
    private String senhaHashFuncionario;

    @Override
    public void run(String... args) {

        if (usuarioRepository.count() > 0) {
            return;
        }

        criarUsuario("Administrador", emailAdmin, senhaHashAdmin, TipoUsuario.ADM);
        criarUsuario("Funcionário", emailFuncionario, senhaHashFuncionario, TipoUsuario.FUNCIONARIO);
    }

    // A senha já vem como hash BCrypt do application.properties, então é gravada como está
    private void criarUsuario(String nome, String email, String senhaHash, TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(nome);
        usuario.setEmailUsuario(email);
        usuario.setSenhaUsuario(senhaHash);
        usuario.setTipoUsuario(tipo);
        usuarioRepository.save(usuario);
    }
}
