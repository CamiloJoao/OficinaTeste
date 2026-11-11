package com.projeto.oficina.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.service.DiagnosticoException;
import com.projeto.oficina.service.DiagnosticoService;
import com.projeto.oficina.service.ServicoService;
import com.projeto.oficina.service.SugestaoDiagnostico;

// Área "Apoio ao diagnóstico" nos detalhes da manutenção
@Controller
@RequestMapping("/statusdeservico")
public class DiagnosticoController {

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private DiagnosticoService diagnosticoService;

    @PostMapping("/diagnostico")
    public String gerarSugestao(
            @RequestParam int idServico,
            @RequestParam(required = false) String equipamento,
            @RequestParam(required = false) String marcaModelo,
            @RequestParam(required = false) String testesRealizados,
            RedirectAttributes redirect) {

        // Sempre volta para a área de diagnóstico dos detalhes
        String destino = "redirect:/statusdeservico/detalhes/" + idServico + "#diagnostico";

        Servico servico = servicoService.buscarPorId(idServico);

        if (servico.getTipoServico() != TipoServico.MANUTENCAO || servico.getStatus() == StatusServico.ENCERRADO) {
            redirect.addFlashAttribute("erroIa", "O apoio ao diagnóstico está disponível só para manutenções em andamento.");
            return destino;
        }

        // Mesmas regras do cadastro da manutenção: equipamento só aceita as opções do select
        equipamento = limparTexto(equipamento);
        if (!"Desktop".equals(equipamento) && !"Notebook".equals(equipamento) && !"Outro".equals(equipamento)) {
            equipamento = null;
        }

        // Mesmo limite da coluna marca_modelo
        marcaModelo = limparTexto(marcaModelo);
        if (marcaModelo != null && marcaModelo.length() > 100) {
            redirect.addFlashAttribute("erroIa", "A marca e o modelo podem ter no máximo 100 caracteres.");
            return destino;
        }

        // Mesmo limite da coluna testes_realizados
        testesRealizados = limparTexto(testesRealizados);
        if (testesRealizados != null && testesRealizados.length() > 200) {
            redirect.addFlashAttribute("erroIa", "O campo 'O que já foi testado' pode ter no máximo 200 caracteres.");
            return destino;
        }

        // Grava antes de chamar a IA: os campos ficam mesmo se a consulta falhar
        servico.setEquipamento(equipamento);
        servico.setMarcaModelo(marcaModelo);
        servico.setTestesRealizados(testesRealizados);
        servicoService.salvar(servico);

        // A sugestão vai só para a próxima página (flash) e não é gravada no banco
        try {
            SugestaoDiagnostico sugestao = diagnosticoService.gerar(servico);
            redirect.addFlashAttribute("sugestaoIa", sugestao);
        } catch (DiagnosticoException e) {
            redirect.addFlashAttribute("erroIa", e.getMessage());
        }

        return destino;
    }

    // Tira os espaços das pontas; texto vazio vira null
    private String limparTexto(String texto) {
        if (texto == null) {
            return null;
        }
        texto = texto.trim();
        if (texto.isEmpty()) {
            return null;
        }
        return texto;
    }
}
