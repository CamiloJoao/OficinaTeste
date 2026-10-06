package com.projeto.oficina.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.service.ServicoService;

@Controller
public class RelatorioController {

    @Autowired
    private ServicoService servicoService;

    // Nomes dos meses para o titulo do relatorio
    private static final String[] NOMES_MESES = {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    // =========================
    // RELATORIO MENSAL
    // =========================
    @GetMapping("/relatorio")
    public String relatorio(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Long ano,
            Model model) {

        LocalDate hoje = LocalDate.now();

        // Sem mes (ou mes invalido), usa o mes atual
        if (mes == null || mes < 1 || mes > 12) {
            mes = hoje.getMonthValue();
        }

        // Sem ano (ou fora de 2000 a 2100), usa o ano atual
        // Long porque um numero muito grande nao cabe em Integer
        if (ano == null || ano < 2000 || ano > 2100) {
            ano = (long) hoje.getYear();
        }

        // Primeiro e ultimo dia do mes escolhido
        LocalDate inicio = LocalDate.of(ano.intValue(), mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        List<Servico> servicos = servicoService.listarPorPeriodo(inicio, fim);

        // =========================
        // CONTADORES
        // =========================
        int manutencoes = 0;
        int montagens = 0;
        int encerrados = 0;
        int emAndamento = 0;
        double valorEncerrado = 0;
        double valorEmAndamento = 0;

        for (Servico s : servicos) {

            if (s.getTipoServico() == TipoServico.MANUTENCAO) {
                manutencoes++;
            } else if (s.getTipoServico() == TipoServico.MONTAGEM) {
                montagens++;
            }

            // Soma o valor separado por status (ignora servicos sem orcamento final)
            if (s.getStatus() == StatusServico.ENCERRADO) {
                encerrados++;
                if (s.getOrcamentoFinal() != null) {
                    valorEncerrado += s.getOrcamentoFinal();
                }
            } else if (s.getStatus() == StatusServico.EM_ANDAMENTO) {
                emAndamento++;
                if (s.getOrcamentoFinal() != null) {
                    valorEmAndamento += s.getOrcamentoFinal();
                }
            }
        }

        // =========================
        // MANDAR PRO FRONT
        // =========================
        model.addAttribute("mes", mes);
        model.addAttribute("ano", ano);
        model.addAttribute("nomeMes", NOMES_MESES[mes - 1]);

        model.addAttribute("servicos", servicos);
        model.addAttribute("total", servicos.size());
        model.addAttribute("manutencoes", manutencoes);
        model.addAttribute("montagens", montagens);
        model.addAttribute("encerrados", encerrados);
        model.addAttribute("emAndamento", emAndamento);
        model.addAttribute("valorEncerrado", valorEncerrado);
        model.addAttribute("valorEmAndamento", valorEmAndamento);

        model.addAttribute("pagina", "relatorio");

        return "layout";
    }
}
