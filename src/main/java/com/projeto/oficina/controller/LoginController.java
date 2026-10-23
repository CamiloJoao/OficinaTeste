package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Página própria, sem layout: quem ainda não entrou não vê o menu
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
