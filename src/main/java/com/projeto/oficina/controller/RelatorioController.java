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
            @RequestParam(required = false) Integer ano,
            Model model) {

        LocalDate hoje = LocalDate.now();

        // Sem mes (ou mes invalido), usa o mes atual
        if (mes == null || mes < 1 || mes > 12) {
            mes = hoje.getMonthValue();
        }

        // Sem ano, usa o ano atual
        if (ano == null) {
            ano = hoje.getYear();
        }

        // Primeiro e ultimo dia do mes escolhido
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        List<Servico> servicos = servicoService.listarPorPeriodo(inicio, fim);

        // =========================
        // CONTADORES
        // =========================
        int manutencoes = 0;
        int montagens = 0;
        int encerrados = 0;
        int emAndamento = 0;
        double valorTotal = 0;

        for (Servico s : servicos) {

            if (s.getTipoServico() == TipoServico.MANUTENCAO) {
                manutencoes++;
            } else if (s.getTipoServico() == TipoServico.MONTAGEM) {
                montagens++;
            }

            if (s.getStatus() == StatusServico.ENCERRADO) {
                encerrados++;
            } else if (s.getStatus() == StatusServico.EM_ANDAMENTO) {
                emAndamento++;
            }

            // Ignora servicos sem orcamento final
            if (s.getOrcamentoFinal() != null) {
                valorTotal += s.getOrcamentoFinal();
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
        model.addAttribute("valorTotal", valorTotal);

        model.addAttribute("pagina", "relatorio");

        return "layout";
    }
}
