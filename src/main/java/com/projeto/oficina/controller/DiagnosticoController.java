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

    private static final String SO_EM_ANDAMENTO = "O apoio ao diagnóstico está disponível só para manutenções em andamento.";

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

        if (!manutencaoEmAndamento(servico)) {
            redirect.addFlashAttribute("erroIa", SO_EM_ANDAMENTO);
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

    @PostMapping("/diagnostico-confirmado")
    public String salvarDiagnosticoConfirmado(
            @RequestParam int idServico,
            @RequestParam(required = false) String diagnosticoConfirmado,
            RedirectAttributes redirect) {

        String destino = "redirect:/statusdeservico/detalhes/" + idServico + "#diagnostico";

        Servico servico = servicoService.buscarPorId(idServico);

        if (!manutencaoEmAndamento(servico)) {
            redirect.addFlashAttribute("erroIa", SO_EM_ANDAMENTO);
            return destino;
        }

        diagnosticoConfirmado = limparTexto(diagnosticoConfirmado);

        // O navegador envia cada Enter como \r\n, mas o maxlength do campo conta só 1 caractere
        if (diagnosticoConfirmado != null) {
            diagnosticoConfirmado = diagnosticoConfirmado.replace("\r\n", "\n");
        }

        // Mesmo limite da coluna diagnostico_confirmado
        if (diagnosticoConfirmado != null && diagnosticoConfirmado.length() > 500) {
            redirect.addFlashAttribute("erroIa", "O diagnóstico pode ter no máximo 500 caracteres.");
            return destino;
        }

        servico.setDiagnosticoConfirmado(diagnosticoConfirmado);
        servicoService.salvar(servico);

        redirect.addFlashAttribute("sucessoIa", "Diagnóstico salvo com sucesso!");
        return destino;
    }

    // Só manutenção em andamento usa a área de diagnóstico
    private boolean manutencaoEmAndamento(Servico servico) {
        return servico.getTipoServico() == TipoServico.MANUTENCAO && servico.getStatus() != StatusServico.ENCERRADO;
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
