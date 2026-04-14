package com.projeto.oficina.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.service.ClienteService;
import com.projeto.oficina.service.ServicoService;

@Controller
@RequestMapping("/manutencao")
public class ManutencaoController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicoService servicoService;

    @GetMapping
    public String manutencao(Model model) {
        model.addAttribute("pagina", "manutencao");
        return "layout";
    }

   @PostMapping("/salvar")
    public String salvar(
            @RequestParam String emailCliente,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String problemaSelecionado,
            @RequestParam String dataPrevista,
            @RequestParam Double orcamento,
            Model model) {

        // VALIDAR EMAIL
        if (emailCliente == null || emailCliente.trim().isEmpty()) {
            model.addAttribute("erro", "Digite o email do cliente.");
            model.addAttribute("pagina", "manutencao");
            return "layout";
        }

        Cliente cliente = clienteService.buscarPorEmail(emailCliente);

        if (cliente == null) {
            model.addAttribute("erro", "E-mail não cadastrado.");
            model.addAttribute("pagina", "manutencao");
            return "layout";
        }

        // VALIDAR PROBLEMA
        if ((problemaSelecionado == null || problemaSelecionado.isEmpty()) &&
            (descricao == null || descricao.isEmpty())) {

            model.addAttribute("erro", "Informe ou selecione um problema.");
            model.addAttribute("pagina", "manutencao");
            return "layout";
        }

        // DESCRIÇÃO FINAL
        String descricaoFinal = "";

        if (problemaSelecionado != null && !problemaSelecionado.isEmpty()) {
            descricaoFinal = problemaSelecionado;
        }

        if (descricao != null && !descricao.isEmpty()) {
            descricaoFinal += " - " + descricao;
        }

        // VALIDAR DATA
        java.time.LocalDate data;
        try {
            data = java.time.LocalDate.parse(dataPrevista);
        } catch (Exception e) {
            model.addAttribute("erro", "Data inválida.");
            model.addAttribute("pagina", "manutencao");
            return "layout";
        }

        // SALVAR
        Servico servico = new Servico();
        servico.setCliente(cliente);
        servico.setTipoServico(TipoServico.MANUTENCAO);
        servico.setDescricaoProblema(descricaoFinal);
        servico.setStatus(StatusServico.EM_ANDAMENTO);
        servico.setOrcamentoInicial(orcamento);
        servico.setOrcamentoFinal(orcamento); 
        servico.setDataPrevistaConclusao(data);

        servicoService.salvar(servico);

        model.addAttribute("sucesso", "Manutenção cadastrada com sucesso!");
        model.addAttribute("pagina", "manutencao");

        return "layout";
    }
}
