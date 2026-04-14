package com.projeto.oficina.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.oficina.compatibilidade.*;
import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.model.ServicoPeca;
import com.projeto.oficina.service.ClienteService;
import com.projeto.oficina.service.ServicoService;

@Controller
@RequestMapping("/statusdeservico")
public class StatusController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private CompatibilidadeService compatibilidadeService;

    @Autowired
    private CompatibilidadeRegras compatibilidadeRegras;

    // =========================
    // TELA PRINCIPAL
    // =========================
    @GetMapping
    public String status(Model model) {
        model.addAttribute("pagina", "statusdeservico");
        return "layout";
    }

    // =========================
    // BUSCAR POR EMAIL
    // =========================
    @GetMapping("/buscar")
    public String buscarPorEmail(@RequestParam String emailCliente, Model model) {

        Cliente cliente = clienteService.buscarPorEmail(emailCliente);

        if (cliente == null) {
            model.addAttribute("erro", "Cliente não encontrado.");
            model.addAttribute("pagina", "statusdeservico");
            return "layout";
        }

        List<Servico> servicos = servicoService.buscarPorCliente(cliente);

        model.addAttribute("servicos", servicos);
        model.addAttribute("pagina", "statusdeservico");

        return "layout";
    }

    // =========================
    // DETALHES
    // =========================
    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable int id, Model model) {

        Servico servico = servicoService.buscarPorId(id);
        model.addAttribute("servico", servico);

        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        List<ServicoPeca> extras = servico.getPecas()
            .stream()
            .filter(p -> p.getTipo() == null)
            .toList();

        double totalExtras = extras.stream()
            .mapToDouble(p -> p.getValor() != null ? p.getValor() : 0)
            .sum();

        model.addAttribute("pecasExtras", extras);
        model.addAttribute("totalExtras", totalExtras);

        model.addAttribute("placaMae", data.getPlaca_mae());
        model.addAttribute("processador", data.getProcessador());
        model.addAttribute("memoriaRam", data.getMemoria_ram());
        model.addAttribute("armazenamento", data.getArmazenamento());

        // 
        Integer placaAtual = null;
        Integer processadorAtual = null;
        Integer ramAtual = null;
        Integer armazenamentoAtual = null;

        for (ServicoPeca p : servico.getPecas()) {
            if (p.getTipo() == null) continue;

            switch (p.getTipo()) {
                case "placa_mae":
                    placaAtual = p.getIdReferencia();
                    break;
                case "processador":
                    processadorAtual = p.getIdReferencia();
                    break;
                case "memoria_ram":
                    ramAtual = p.getIdReferencia();
                    break;
                case "armazenamento":
                    armazenamentoAtual = p.getIdReferencia();
                    break;
            }
        }

        model.addAttribute("placaAtual", placaAtual);
        model.addAttribute("processadorAtual", processadorAtual);
        model.addAttribute("ramAtual", ramAtual);
        model.addAttribute("armazenamentoAtual", armazenamentoAtual);
        

        if (servico.getTipoServico() == TipoServico.MONTAGEM) {
            model.addAttribute("pagina", "detalhe-montagem");
        } else {
            model.addAttribute("pagina", "detalhe-manutencao");
        }

        return "layout";
    }

    // =========================
    // EDITAR PEÇA (UMA LINHA)
    // =========================
    @PostMapping("/editar-peca-tipo")
    public String editarPorTipo(
            @RequestParam int idServico,
            @RequestParam String tipo,
            @RequestParam int idReferencia) {

        Servico servico = servicoService.buscarPorId(idServico);
        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        String nome = "";
        Double valor = 0.0;

        if (tipo.equals("placa_mae")) {
            var p = compatibilidadeService.buscarPorId(
                    data.getPlaca_mae(), x -> x.getId_placamae() == idReferencia);
            nome = "Placa: " + p.getModelo();
            valor = p.getPreco();
        }

        if (tipo.equals("processador")) {
            var p = compatibilidadeService.buscarPorId(
                    data.getProcessador(), x -> x.getId_processador() == idReferencia);
            nome = "CPU: " + p.getModelo();
            valor = p.getPreco();
        }

        if (tipo.equals("memoria_ram")) {
            var p = compatibilidadeService.buscarPorId(
                    data.getMemoria_ram(), x -> x.getId_memoriaRam() == idReferencia);
            nome = "RAM: " + p.getModelo();
            valor = p.getPreco();
        }

        if (tipo.equals("armazenamento")) {
            var p = compatibilidadeService.buscarPorId(
                    data.getArmazenamento(), x -> x.getId_armazenamento() == idReferencia);
            nome = "Armazenamento: " + p.getModelo();
            valor = p.getPreco();
        }

        // REMOVE antiga (evita null pointer)
        servico.getPecas().removeIf(p -> tipo.equals(p.getTipo()));

        // NOVA PEÇA
        ServicoPeca nova = new ServicoPeca();
        nova.setServico(servico);
        nova.setNomePeca(nome);
        nova.setTipo(tipo);
        nova.setIdReferencia(idReferencia);
        nova.setValor(valor);

        servico.getPecas().add(nova);

        recalcularOrcamento(servico);

        servicoService.salvar(servico);

        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    // =========================
    // EDITAR TUDO (BOTÃO ÚNICO)
    // =========================
     @PostMapping("/editar-lote")
    public String editarLote(
            @RequestParam int idServico,
            @RequestParam int placaMae,
            @RequestParam int processador,
            @RequestParam int memoriaRam,
            @RequestParam int armazenamento,
            RedirectAttributes redirect) {

        Servico servico = servicoService.buscarPorId(idServico);

        // VERIFICAR MUDANÇA
        Integer placaAtual = getAtual(servico, "placa_mae");
        Integer procAtual = getAtual(servico, "processador");
        Integer ramAtual = getAtual(servico, "memoria_ram");
        Integer armAtual = getAtual(servico, "armazenamento");

        boolean mudou =
                !equalsNullable(placaAtual, placaMae) ||
                !equalsNullable(procAtual, processador) ||
                !equalsNullable(ramAtual, memoriaRam) ||
                !equalsNullable(armAtual, armazenamento);

        if (!mudou) {
            redirect.addFlashAttribute("msg", "Nenhuma alteração foi feita.");
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        // CARREGAR DADOS
        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        PlacaMae placa = compatibilidadeService.buscarPorId(
                data.getPlaca_mae(), p -> p.getId_placamae() == placaMae);

        Processador proc = compatibilidadeService.buscarPorId(
                data.getProcessador(), p -> p.getId_processador() == processador);

        MemoriaRam ram = compatibilidadeService.buscarPorId(
                data.getMemoria_ram(), m -> m.getId_memoriaRam() == memoriaRam);

        Armazenamento arm = compatibilidadeService.buscarPorId(
                data.getArmazenamento(), a -> a.getId_armazenamento() == armazenamento);

        // VALIDAR COMPATIBILIDADE
        boolean compativel = compatibilidadeRegras.isCompativel(
                placa, proc, ram, arm);

        if (!compativel) {
            redirect.addFlashAttribute("msgErro", "Peças não compatíveis");
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        // REMOVER PEÇAS BASE
        List<ServicoPeca> paraRemover = servico.getPecas()
                .stream()
                .filter(p -> p.getTipo() != null)
                .toList();

        for (ServicoPeca p : paraRemover) {
            servico.removerPeca(p);
        }

        // ADICIONAR NOVAS
        adicionarPeca(servico, "placa_mae", placaMae, data);
        adicionarPeca(servico, "processador", processador, data);
        adicionarPeca(servico, "memoria_ram", memoriaRam, data);
        adicionarPeca(servico, "armazenamento", armazenamento, data);

        recalcularOrcamento(servico);

        servicoService.salvar(servico);

        redirect.addFlashAttribute("msg", "Configuração atualizada com sucesso!");

        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    // =========================
    // ADICIONAR PEÇA EXTRA (JSON)
    // =========================
    @PostMapping("/adicionar-peca-json")
    public String adicionarPecaJson(
            @RequestParam int idServico,
            @RequestParam String tipo,
            @RequestParam int idReferencia) {

        Servico servico = servicoService.buscarPorId(idServico);
        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        String nome = "";
        Double valor = 0.0;

        if ("placa_mae".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getPlaca_mae(), x -> x.getId_placamae() == idReferencia);

            nome = "Placa (extra): " + p.getModelo();
            valor = p.getPreco();
        }

        else if ("processador".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getProcessador(), x -> x.getId_processador() == idReferencia);

            nome = "CPU (extra): " + p.getModelo();
            valor = p.getPreco();
        }

        else if ("memoria_ram".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getMemoria_ram(), x -> x.getId_memoriaRam() == idReferencia);

            nome = "RAM (extra): " + p.getModelo();
            valor = p.getPreco();
        }

        else if ("armazenamento".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getArmazenamento(), x -> x.getId_armazenamento() == idReferencia);

            nome = "Armazenamento (extra): " + p.getModelo();
            valor = p.getPreco();
        }

        ServicoPeca nova = new ServicoPeca();
        nova.setServico(servico);
        nova.setNomePeca(nome);
        nova.setValor(valor);
        nova.setTipo(null); // continua sendo extra
        nova.setIdReferencia(idReferencia);

        servico.getPecas().add(nova);

        recalcularOrcamento(servico);
        servicoService.salvar(servico);

        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    // =========================
    // ENCERRAR
    // =========================
    @PostMapping("/encerrar")
    public String encerrar(@RequestParam int idServico) {

        Servico servico = servicoService.buscarPorId(idServico);
        servico.setStatus(StatusServico.ENCERRADO);

        servicoService.salvar(servico);

        return "redirect:/statusdeservico";
    }

    // =========================
    // MÉTODOS AUXILIARES 
    // =========================
    private Integer getAtual(Servico servico, String tipo) {
        return servico.getPecas().stream()
                .filter(p -> tipo.equals(p.getTipo()))
                .map(ServicoPeca::getIdReferencia)
                .findFirst()
                .orElse(null);
    }

    private boolean equalsNullable(Integer a, Integer b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private void adicionarPeca(Servico servico, String tipo, int idReferencia, CompatibilidadeData data) {

        String nome = "";
        Double valor = 0.0;

        if ("placa_mae".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getPlaca_mae(), x -> x.getId_placamae() == idReferencia);

            nome = "Placa: " + p.getModelo();
            valor = p.getPreco();
        }

        else if ("processador".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getProcessador(), x -> x.getId_processador() == idReferencia);

            nome = "CPU: " + p.getModelo();
            valor = p.getPreco();
        }

        else if ("memoria_ram".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getMemoria_ram(), x -> x.getId_memoriaRam() == idReferencia);

            nome = "RAM: " + p.getModelo();
            valor = p.getPreco();
        }

        else if ("armazenamento".equals(tipo)) {
            var p = compatibilidadeService.buscarPorId(
                    data.getArmazenamento(), x -> x.getId_armazenamento() == idReferencia);

            nome = "Armazenamento: " + p.getModelo();
            valor = p.getPreco();
        }

        ServicoPeca nova = new ServicoPeca();
        nova.setServico(servico);
        nova.setNomePeca(nome);
        nova.setValor(valor);
        nova.setTipo(tipo);
        nova.setIdReferencia(idReferencia);

        servico.adicionarPeca(nova); 
    }


    //RECALCULAR ORCAMENTO
    private void recalcularOrcamento(Servico servico) {

        double total = servico.getOrcamentoInicial();

        for (ServicoPeca p : servico.getPecas()) {

            // 🔥 SOMENTE peças extras (sem tipo)
            if (p.getTipo() == null && p.getValor() != null) {
                total += p.getValor();
            }
        }

        servico.setOrcamentoFinal(total);
    }
}
