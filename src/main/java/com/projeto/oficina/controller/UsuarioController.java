package com.projeto.oficina.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.oficina.enums.TipoUsuario;
import com.projeto.oficina.model.Usuario;
import com.projeto.oficina.service.UsuarioService;

@Controller
@RequestMapping("/gerenciarusuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("pagina", "gerenciarusuarios");
        return "layout";
    }

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

    // Formulário de edição (a senha nunca vai para a página)
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model, RedirectAttributes redirect) {

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            redirect.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/gerenciarusuarios";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "editarusuario");
        return "layout";
    }

    // Atualizar usuário: mesmas validações do cadastro, mas a senha é opcional
    @PostMapping("/atualizar")
    public String atualizarUsuario(
            @RequestParam Integer idUsuario,
            @RequestParam String nomeUsuario,
            @RequestParam String emailUsuario,
            @RequestParam String senhaUsuario,
            @RequestParam TipoUsuario tipoUsuario,
            Model model,
            RedirectAttributes redirect) {

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        if (usuario == null) {
            redirect.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/gerenciarusuarios";
        }

        String erro = null;

        if (nomeUsuario.trim().isEmpty() || emailUsuario.trim().isEmpty()) {
            erro = "Preencha nome e e-mail.";
        } else {
            // O e-mail só é recusado se for de OUTRO usuário
            Usuario donoDoEmail = usuarioService.buscarPorEmail(emailUsuario.trim());
            if (donoDoEmail != null && !donoDoEmail.getIdUsuario().equals(idUsuario)) {
                erro = "E-mail já cadastrado.";
            }
        }

        // O sistema nunca pode ficar sem administrador
        if (erro == null && tipoUsuario == TipoUsuario.FUNCIONARIO && usuarioService.ehUltimoAdministrador(usuario)) {
            erro = "O sistema precisa ter pelo menos um administrador.";
        }

        if (erro != null) {
            // Volta para o formulário com o que foi digitado (a senha volta vazia)
            Usuario digitado = new Usuario();
            digitado.setIdUsuario(idUsuario);
            digitado.setNomeUsuario(nomeUsuario);
            digitado.setEmailUsuario(emailUsuario);
            digitado.setTipoUsuario(tipoUsuario);

            model.addAttribute("usuario", digitado);
            model.addAttribute("erro", erro);
            model.addAttribute("pagina", "editarusuario");
            return "layout";
        }

        usuario.setNomeUsuario(nomeUsuario.trim());
        usuario.setEmailUsuario(emailUsuario.trim());
        usuario.setTipoUsuario(tipoUsuario);
        usuarioService.atualizar(usuario, senhaUsuario);

        redirect.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
        return "redirect:/gerenciarusuarios";
    }

    // Excluir usuário (form POST com confirmação na tela)
    @PostMapping("/excluir")
    public String excluirUsuario(@RequestParam Integer idUsuario, Principal principal, RedirectAttributes redirect) {

        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        if (usuario == null) {
            redirect.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/gerenciarusuarios";
        }

        // principal.getName() é o e-mail de quem está logado
        if (usuario.getEmailUsuario().equals(principal.getName())) {
            redirect.addFlashAttribute("erro", "Você não pode excluir a sua própria conta.");
            return "redirect:/gerenciarusuarios";
        }

        // O sistema nunca pode ficar sem administrador
        if (usuarioService.ehUltimoAdministrador(usuario)) {
            redirect.addFlashAttribute("erro", "O sistema precisa ter pelo menos um administrador.");
            return "redirect:/gerenciarusuarios";
        }

        usuarioService.excluir(idUsuario);

        redirect.addFlashAttribute("mensagem", "Usuário excluído com sucesso!");
        return "redirect:/gerenciarusuarios";
    }
}
