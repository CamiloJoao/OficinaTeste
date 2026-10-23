package com.projeto.oficina.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.oficina.enums.TipoUsuario;
import com.projeto.oficina.model.Usuario;
import com.projeto.oficina.service.UsuarioService;

@Controller
@RequestMapping("/gerenciarusuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Tela principal (lista de usuários)
    @GetMapping
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("pagina", "gerenciarusuarios");
        return "layout";
    }

    // Formulário de novo usuário
    @GetMapping("/cadastrar")
    public String novoUsuario(Model model) {
        model.addAttribute("pagina", "cadastrousuario");
        return "layout";
    }

    // Salvar novo usuário, com validação no servidor (RNF07).
    // Cada campo vem separado: o formulário não consegue mandar um ID e alterar outro usuário.
    @PostMapping("/salvar")
    public String salvarUsuario(
            @RequestParam String nomeUsuario,
            @RequestParam String emailUsuario,
            @RequestParam String senhaUsuario,
            @RequestParam TipoUsuario tipoUsuario,
            Model model) {

        model.addAttribute("pagina", "cadastrousuario");

        String erro = null;

        // Campo só com espaços conta como vazio
        if (nomeUsuario.trim().isEmpty() || emailUsuario.trim().isEmpty() || senhaUsuario.trim().isEmpty()) {
            erro = "Preencha nome, e-mail e senha.";
        } else if (usuarioService.buscarPorEmail(emailUsuario.trim()) != null) {
            erro = "E-mail já cadastrado.";
        }

        if (erro != null) {
            // Devolve o que foi digitado (menos a senha) para não precisar digitar de novo
            model.addAttribute("erro", erro);
            model.addAttribute("nomeUsuario", nomeUsuario);
            model.addAttribute("emailUsuario", emailUsuario);
            model.addAttribute("tipoUsuario", tipoUsuario.name());
            return "layout";
        }

        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(nomeUsuario.trim());
        usuario.setEmailUsuario(emailUsuario.trim());
        usuario.setSenhaUsuario(senhaUsuario); // como foi digitada; o service criptografa
        usuario.setTipoUsuario(tipoUsuario);
        usuarioService.salvar(usuario);

        model.addAttribute("mensagem", "Usuário cadastrado com sucesso!");
        return "layout";
    }
}
