package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PainelController {

    @GetMapping("/")
    public String painel(Model model) {
        model.addAttribute("pagina", "painelgeral");
        return "layout";
    }
}