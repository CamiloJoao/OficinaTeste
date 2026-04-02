package com.projeto.oficina.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.oficina.compatibilidade.*;

@Controller
public class MontagemController {

    @Autowired
    private CompatibilidadeService compatibilidadeService;

    @Autowired
    private CompatibilidadeRegras compatibilidadeRegras;

    // =========================
    // 🔹 TELA INICIAL
    // =========================
    @GetMapping("/montagem")
    public String montagem(Model model) {

        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        model.addAttribute("placaMae", data.getPlaca_mae());
        model.addAttribute("processador", data.getProcessador());
        model.addAttribute("memoriaRam", data.getMemoria_ram());
        model.addAttribute("armazenamento", data.getArmazenamento());
        model.addAttribute("pagina", "montagem");
        return "layout";

        
    }

    // =========================
    // 🔹 VERIFICAR COMPATIBILIDADE
    // =========================
    @PostMapping("/montagem")
    public String checarCompatibilidade(
            @RequestParam("placaMae") int idPlacaMae,
            @RequestParam("processador") int idProcessador,
            @RequestParam("memoriaRam") int idRam,
            @RequestParam("armazenamento") int idArmazenamento,
            Model model) {

        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        // Recarrega listas (para não sumirem no HTML)
        model.addAttribute("placaMae", data.getPlaca_mae());
        model.addAttribute("processador", data.getProcessador());
        model.addAttribute("memoriaRam", data.getMemoria_ram());
        model.addAttribute("armazenamento", data.getArmazenamento());

        // =========================
        // 🔹 BUSCAR PEÇAS (AGORA LIMPO)
        // =========================
        PlacaMae placaMae = compatibilidadeService.buscarPorId(
                data.getPlaca_mae(),
                p -> p.getId_placamae() == idPlacaMae
        );

        Processador processador = compatibilidadeService.buscarPorId(
                data.getProcessador(),
                p -> p.getId_processador() == idProcessador
        );

        MemoriaRam memoriaRam = compatibilidadeService.buscarPorId(
                data.getMemoria_ram(),
                m -> m.getId_memoriaRam() == idRam
        );

        Armazenamento armazenamento = compatibilidadeService.buscarPorId(
                data.getArmazenamento(),
                a -> a.getId_armazenamento() == idArmazenamento
        );

        // =========================
        // 🔹 VERIFICAR COMPATIBILIDADE (SIMPLIFICADO)
        // =========================
        boolean compativel = placaMae != null
                && processador != null
                && memoriaRam != null
                && armazenamento != null
                && compatibilidadeRegras.isCompativel(
                        placaMae, processador, memoriaRam, armazenamento
                );

        // =========================
        // 🔹 RESULTADO
        // =========================
        model.addAttribute("resultado",
                compativel ? "Peças compatíveis ✅" : "Peças incompatíveis ❌");

        if (compativel) {
            model.addAttribute("placaMaeSelecionada", placaMae);
            model.addAttribute("processadorSelecionado", processador);
            model.addAttribute("memoriaRamSelecionada", memoriaRam);
            model.addAttribute("armazenamentoSelecionado", armazenamento);
        }
        model.addAttribute("pagina", "montagem");

        return "layout";
    }
}

