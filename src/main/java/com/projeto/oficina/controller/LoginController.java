package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    // Página própria, sem layout: quem ainda não entrou não vê o menu
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // O Spring Security encaminha para cá quando o perfil não pode abrir a tela.
    // @RequestMapping aceita qualquer método: o encaminhamento de um POST negado chega como POST.
    @RequestMapping("/acesso-negado")
    public String acessoNegado(Model model) {
        model.addAttribute("pagina", "acesso-negado");
        return "layout";
    }
}
