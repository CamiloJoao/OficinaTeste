package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ServicoController {

    @GetMapping("/statusdeservico")
    public String status(Model model) {
        model.addAttribute("pagina", "statusdeservico");
        return "layout";
    }

}
