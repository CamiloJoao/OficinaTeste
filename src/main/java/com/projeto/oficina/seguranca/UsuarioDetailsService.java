package com.projeto.oficina.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projeto.oficina.model.Usuario;
import com.projeto.oficina.repository.UsuarioRepository;

// O Spring Security chama esta classe no login para buscar o usuário pelo e-mail
@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmailUsuario(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        // roles("ADM") vira ROLE_ADM e roles("FUNCIONARIO") vira ROLE_FUNCIONARIO
        return User.withUsername(usuario.getEmailUsuario())
                .password(usuario.getSenhaUsuario())
                .roles(usuario.getTipoUsuario().name())
                .build();
    }
}
