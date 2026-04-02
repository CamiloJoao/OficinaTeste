package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @GetMapping("/gerenciarusuarios")
    public String usuarios(Model model) {
        model.addAttribute("pagina", "gerenciarusuarios");
        return "layout";
    }
}
