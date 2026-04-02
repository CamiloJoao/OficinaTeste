package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ManutencaoController {

    @GetMapping("/manutencao")
    public String manutencao(Model model) {
        model.addAttribute("pagina", "manutencao");
        return "layout";
    }

}
