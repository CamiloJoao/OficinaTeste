package com.projeto.oficina.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

        int emAndamento = 0;
        int manutencao = 0;
        int montagem = 0;

        for (Servico s : todos) {
            if (s.getStatus() == StatusServico.ENCERRADO) continue;

            emAndamento++;

            if (s.getTipoServico() == TipoServico.MANUTENCAO) {
                manutencao++;
            } else if (s.getTipoServico() == TipoServico.MONTAGEM) {
                montagem++;
            }
        }

        LocalDate hoje = LocalDate.now();

        long noPrazo = 0;
        long proximos = 0;
        long prioritarios = 0;

        for (Servico s : todos) {
            if (s.getStatus() == StatusServico.ENCERRADO) continue;
            if (s.getDataPrevistaConclusao() == null) continue;

            String prazo = classificarPrazo(s.getDataPrevistaConclusao(), hoje);
            if (prazo.equals("prioritario")) {
                prioritarios++;
            } else if (prazo.equals("proximo")) {
                proximos++;
            } else {
                noPrazo++;
            }
        }

        // Semana atual: de segunda a sábado
        LocalDate inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fimSemana = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        Map<String, Integer> semana = new HashMap<>();

        semana.put("Segunda", 0);
        semana.put("Terça", 0);
        semana.put("Quarta", 0);
        semana.put("Quinta", 0);
        semana.put("Sexta", 0);
        semana.put("Sábado", 0);

        for (Servico s : todos) {
            if (s.getDataPrevistaConclusao() == null) continue;

            if (s.getStatus() == StatusServico.ENCERRADO) continue;

            LocalDate data = s.getDataPrevistaConclusao();

            // Só conta se a data cair dentro da semana atual
            if (data.isBefore(inicioSemana) || data.isAfter(fimSemana)) {
                continue;
            }

            switch (data.getDayOfWeek()) {
                case MONDAY -> semana.put("Segunda", semana.get("Segunda") + 1);
                case TUESDAY -> semana.put("Terça", semana.get("Terça") + 1);
                case WEDNESDAY -> semana.put("Quarta", semana.get("Quarta") + 1);
                case THURSDAY -> semana.put("Quinta", semana.get("Quinta") + 1);
                case FRIDAY -> semana.put("Sexta", semana.get("Sexta") + 1);
                case SATURDAY -> semana.put("Sábado", semana.get("Sábado") + 1);
                default -> {} // ignora domingo
            }
        }

        // Data de cada dia da semana, para os links dos cards
        Map<String, LocalDate> diasData = new LinkedHashMap<>();
        diasData.put("Segunda", inicioSemana);
        diasData.put("Terça", inicioSemana.plusDays(1));
        diasData.put("Quarta", inicioSemana.plusDays(2));
        diasData.put("Quinta", inicioSemana.plusDays(3));
        diasData.put("Sexta", inicioSemana.plusDays(4));
        diasData.put("Sábado", inicioSemana.plusDays(5));

        List<Servico> ultimos = servicoService.buscarUltimos();

        model.addAttribute("emAndamento", emAndamento);
        model.addAttribute("montagens", montagem);
        model.addAttribute("manutencoes", manutencao);

        model.addAttribute("noPrazo", noPrazo);
        model.addAttribute("proximos", proximos);
        model.addAttribute("prioritarios", prioritarios);

        model.addAttribute("semana", semana);
        model.addAttribute("diasData", diasData);

        model.addAttribute("ultimosServicos", ultimos);

        model.addAttribute("pagina", "painelgeral");

        return "layout";
    }

    @GetMapping("/painel/prazos")
    public String listarPorPrazo(@RequestParam String tipo, Model model) {

        List<Servico> todos = servicoService.listarTodos();
        LocalDate hoje = LocalDate.now();

        List<Servico> filtrados = new ArrayList<>();
        for (Servico s : todos) {
            if (s.getStatus() == StatusServico.ENCERRADO) continue;
            if (s.getDataPrevistaConclusao() == null) continue;

            if (classificarPrazo(s.getDataPrevistaConclusao(), hoje).equals(tipo)) {
                filtrados.add(s);
            }
        }

        model.addAttribute("servicos", filtrados);
        model.addAttribute("tipoSelecionado", tipo);

        model.addAttribute("pagina", "painel-prazos");

        return "layout";
    }

    @GetMapping("/painel/dia")
    public String listarPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) {

        List<Servico> todos = servicoService.listarTodos();

        List<Servico> filtrados = new ArrayList<>();
        for (Servico s : todos) {
            if (s.getStatus() != StatusServico.ENCERRADO && data.equals(s.getDataPrevistaConclusao())) {
                filtrados.add(s);
            }
        }

        model.addAttribute("servicos", filtrados);
        model.addAttribute("dataSelecionada", data);

        model.addAttribute("pagina", "painel-dia");

        return "layout";
    }

    // Prioritário: vence hoje ou já venceu; próximo: vence em até 3 dias; normal: depois disso
    private String classificarPrazo(LocalDate data, LocalDate hoje) {
        if (!data.isAfter(hoje)) {
            return "prioritario";
        }
        if (!data.isAfter(hoje.plusDays(3))) {
            return "proximo";
        }
        return "normal";
    }
}