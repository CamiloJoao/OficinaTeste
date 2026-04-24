package com.projeto.oficina.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.service.ServicoService;

@Controller
public class PainelController {

    @Autowired
    private ServicoService servicoService;

    @GetMapping("/")
    public String painel(Model model) {


        List<Servico> todos = servicoService.listarTodos();

        // =========================
        // CONTADORES
        // =========================
        long emAndamento = todos.stream()
                .filter(s -> s.getStatus() == StatusServico.EM_ANDAMENTO)
                .count();

        int manutencao = 0;
        int montagem = 0;

        for (Servico s : todos) {

            if (s.getStatus() == StatusServico.ENCERRADO) continue;

            if (s.getTipoServico() == TipoServico.MANUTENCAO) {
                manutencao++;
            } else if (s.getTipoServico() == TipoServico.MONTAGEM) {
                montagem++;
            }
        }

        // =========================
        // PRAZOS
        // =========================
        LocalDate hoje = LocalDate.now();

        long noPrazo = 0;
        long proximos = 0;
        long prioritarios = 0;

        for (Servico s : todos) {

            //ignora os encerrados
             if (s.getStatus() == StatusServico.ENCERRADO) {
                continue;
            }

            if (s.getDataPrevistaConclusao() == null) continue;

            LocalDate data = s.getDataPrevistaConclusao();

            if (!data.isAfter(hoje)) {
                prioritarios++;
            } 
            else if (!data.isAfter(hoje.plusDays(3))) {
                proximos++;
            } 
            else {
                noPrazo++;
            }
        }

        // =========================
        // SERVIÇOS DA SEMANA
        // =========================
        Map<String, Integer> semana = new HashMap<>();

        semana.put("Segunda", 0);
        semana.put("Terça", 0);
        semana.put("Quarta", 0);
        semana.put("Quinta", 0);
        semana.put("Sexta", 0);

        for (Servico s : todos) {
            if (s.getDataPrevistaConclusao() == null) continue;

            if (s.getStatus() == StatusServico.ENCERRADO) continue;

           switch (s.getDataPrevistaConclusao().getDayOfWeek()) {
                case MONDAY -> semana.put("Segunda", semana.get("Segunda") + 1);
                case TUESDAY -> semana.put("Terça", semana.get("Terça") + 1);
                case WEDNESDAY -> semana.put("Quarta", semana.get("Quarta") + 1);
                case THURSDAY -> semana.put("Quinta", semana.get("Quinta") + 1);
                case FRIDAY -> semana.put("Sexta", semana.get("Sexta") + 1);
                default -> {} // ignora sabado e domingo
            }
        }

        // =========================
        // ÚLTIMOS SERVIÇOS
        // =========================
        List<Servico> ultimos = servicoService.buscarUltimos();

        // =========================
        // MANDAR PRO FRONT
        // =========================
        model.addAttribute("emAndamento", emAndamento);
        model.addAttribute("montagens", montagem);
        model.addAttribute("manutencoes", manutencao);

        model.addAttribute("noPrazo", noPrazo);
        model.addAttribute("proximos", proximos);
        model.addAttribute("prioritarios", prioritarios);

        model.addAttribute("semana", semana);

        model.addAttribute("ultimosServicos", ultimos);

        model.addAttribute("pagina", "painelgeral");


        return "layout";
    }

    // =========================
    // SUBPÁGINA DE PRAZOS
    // =========================
    @GetMapping("/painel/prazos")
    public String listarPorPrazo(@RequestParam String tipo, Model model) {

        List<Servico> todos = servicoService.listarTodos();
        LocalDate hoje = LocalDate.now();

        List<Servico> filtrados = todos.stream()
            .filter(s -> s.getStatus() != StatusServico.ENCERRADO)
            .filter(s -> s.getDataPrevistaConclusao() != null)
            .filter(s -> {

                LocalDate data = s.getDataPrevistaConclusao();

                switch (tipo) {

                    case "prioritario":
                        return data.isBefore(hoje) || data.isEqual(hoje);

                    case "proximo":
                        return data.isAfter(hoje) && data.isBefore(hoje.plusDays(4));

                    case "normal":
                        return data.isAfter(hoje.plusDays(3));

                    default:
                        return false;
                }
            })
            .toList();

        model.addAttribute("servicos", filtrados);
        model.addAttribute("tipoSelecionado", tipo);

        model.addAttribute("pagina", "painel-prazos");

        return "layout";
    }
}