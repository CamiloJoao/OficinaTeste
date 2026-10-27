package com.projeto.oficina.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    // Categorias que só podem existir uma vez no computador
    private static final Set<String> SINGULARES =
            Set.of("placa_mae", "processador", "placa_devideo", "fonte");

    @GetMapping
    public String status(Model model) {
        model.addAttribute("pagina", "statusdeservico");
        return "layout";
    }

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

    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable int id, Model model) {

        Servico servico = servicoService.buscarPorId(id);
        model.addAttribute("servico", servico);

        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        List<ServicoPeca> extras = new ArrayList<>();
        for (ServicoPeca p : servico.getPecas()) {
            if (p.getTipo() == null) {
                extras.add(p);
            }
        }
        model.addAttribute("pecasExtras", extras);

        model.addAttribute("placaMae", data.getPlaca_mae());
        model.addAttribute("processador", data.getProcessador());
        model.addAttribute("memoriaRam", data.getMemoria_ram());
        model.addAttribute("armazenamento", data.getArmazenamento());
        model.addAttribute("placaDeVideo", data.getPlaca_devideo());
        model.addAttribute("fonte", data.getFonte());

        model.addAttribute("placaAtual", getAtual(servico, "placa_mae"));
        model.addAttribute("processadorAtual", getAtual(servico, "processador"));
        model.addAttribute("ramAtual", getAtual(servico, "memoria_ram"));
        model.addAttribute("armazenamentoAtual", getAtual(servico, "armazenamento"));
        model.addAttribute("placaDeVideoAtual", getAtual(servico, "placa_devideo"));
        model.addAttribute("fonteAtual", getAtual(servico, "fonte"));

        // Categorias únicas que já têm peça (base ou extra)
        Set<String> categoriasOcupadas = new HashSet<>();
        for (String categoria : SINGULARES) {
            if (existeCategoria(servico, categoria, null)) {
                categoriasOcupadas.add(categoria);
            }
        }
        model.addAttribute("categoriasOcupadas", categoriasOcupadas);

        if (servico.getTipoServico() == TipoServico.MONTAGEM) {
            model.addAttribute("pagina", "detalhe-montagem");
        } else {
            model.addAttribute("pagina", "detalhe-manutencao");
        }

        return "layout";
    }

    @PostMapping("/editar-lote")
    public String editarLote(
            @RequestParam int idServico,
            @RequestParam int placaMae,
            @RequestParam int processador,
            @RequestParam int memoriaRam,
            @RequestParam int armazenamento,
            @RequestParam(required = false) Integer placaDeVideo,
            @RequestParam int fonte,
            RedirectAttributes redirect) {

        Servico servico = servicoService.buscarPorId(idServico);

        Integer placaAtual = getAtual(servico, "placa_mae");
        Integer procAtual = getAtual(servico, "processador");
        Integer ramAtual = getAtual(servico, "memoria_ram");
        Integer armAtual = getAtual(servico, "armazenamento");
        Integer gpuAtual = getAtual(servico, "placa_devideo");
        Integer fonteAtual = getAtual(servico, "fonte");

        boolean mudou =
                !Objects.equals(placaAtual, placaMae) ||
                !Objects.equals(procAtual, processador) ||
                !Objects.equals(ramAtual, memoriaRam) ||
                !Objects.equals(armAtual, armazenamento) ||
                !Objects.equals(gpuAtual, placaDeVideo) ||
                !Objects.equals(fonteAtual, fonte);

        if (!mudou) {
            redirect.addFlashAttribute("msg", "Nenhuma alteração foi feita.");
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        PlacaMae placa = compatibilidadeService.buscarPorId(
                data.getPlaca_mae(), p -> p.getId_placamae() == placaMae);

        Processador proc = compatibilidadeService.buscarPorId(
                data.getProcessador(), p -> p.getId_processador() == processador);

        MemoriaRam ram = compatibilidadeService.buscarPorId(
                data.getMemoria_ram(), m -> m.getId_memoriaRam() == memoriaRam);

        Armazenamento arm = compatibilidadeService.buscarPorId(
                data.getArmazenamento(), a -> a.getId_armazenamento() == armazenamento);

        Fonte fnt = compatibilidadeService.buscarPorId(
                data.getFonte(), f -> f.getId_fonte() == fonte);

        PlacadeVideo gpu = null;
        if (placaDeVideo != null) {
            gpu = compatibilidadeService.buscarPorId(
                    data.getPlaca_devideo(), v -> v.getId_placadevideo() == placaDeVideo);
        }

        ResultadoCompatibilidade resultado = compatibilidadeService.verificarMontagemCompleta(
                placa, proc, ram, arm, gpu, fnt);

        if (!resultado.isCompativel()) {
            redirect.addFlashAttribute("msgErro", resultado.getErros());
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        for (ServicoPeca p : new ArrayList<>(servico.getPecas())) {
            if (p.getTipo() != null) {
                servico.removerPeca(p);
            }
        }

        adicionarPeca(servico, "placa_mae", placaMae, data);
        adicionarPeca(servico, "processador", processador, data);
        adicionarPeca(servico, "memoria_ram", memoriaRam, data);
        adicionarPeca(servico, "armazenamento", armazenamento, data);
        adicionarPeca(servico, "fonte", fonte, data);

        if (placaDeVideo != null) {
            adicionarPeca(servico, "placa_devideo", placaDeVideo, data);
        }

        recalcularOrcamento(servico);
        servicoService.salvar(servico);

        redirect.addFlashAttribute("msg", "Configuração atualizada com sucesso!");

        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    @PostMapping("/adicionar-peca-json")
    public String adicionarPecaJson(
            @RequestParam int idServico,
            @RequestParam String tipo,
            @RequestParam int idReferencia,
            RedirectAttributes redirect) {

        Servico servico = servicoService.buscarPorId(idServico);
        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        if (SINGULARES.contains(tipo) && existeCategoria(servico, tipo, null)) {
            redirect.addFlashAttribute("msgErroExtra",
                    List.of("Já existe uma peça do tipo '" + nomeAmigavel(tipo)
                            + "' nessa configuração. Edite a peça existente em vez de adicionar outra."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        ResultadoCompatibilidade compat = validarPecaExtra(servico, data, tipo, idReferencia);
        if (compat != null && !compat.isCompativel()) {
            redirect.addFlashAttribute("msgErroExtra", compat.getErros());
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        NomeValor nv = montarNomeValor(tipo, idReferencia, data, true);

        if (nv == null) {
            redirect.addFlashAttribute("msgErroExtra", List.of("Peça inválida selecionada."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        ServicoPeca nova = new ServicoPeca();
        nova.setNomePeca(nv.nome);
        nova.setValor(nv.valor);
        nova.setCategoriaExtra(tipo);
        nova.setIdReferencia(idReferencia);

        servico.adicionarPeca(nova);

        recalcularOrcamento(servico);
        servicoService.salvar(servico);

        redirect.addFlashAttribute("msg", "Peça extra adicionada com sucesso!");
        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    @PostMapping("/editar-peca-extra")
    public String editarPecaExtra(
            @RequestParam int idServico,
            @RequestParam int idPeca,
            @RequestParam int idReferencia,
            RedirectAttributes redirect) {

        Servico servico = servicoService.buscarPorId(idServico);
        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        ServicoPeca peca = buscarPecaExtra(servico, idPeca);

        if (peca == null) {
            redirect.addFlashAttribute("msgErroExtra", List.of("Peça extra não encontrada."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        String categoria = peca.getCategoriaExtra();

        if (SINGULARES.contains(categoria) && existeCategoria(servico, categoria, idPeca)) {
            redirect.addFlashAttribute("msgErroExtra",
                    List.of("Já existe outra peça do tipo '" + nomeAmigavel(categoria) + "' nessa configuração."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        ResultadoCompatibilidade compat = validarPecaExtra(servico, data, categoria, idReferencia);
        if (compat != null && !compat.isCompativel()) {
            redirect.addFlashAttribute("msgErroExtra", compat.getErros());
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        NomeValor nv = montarNomeValor(categoria, idReferencia, data, true);

        if (nv == null) {
            redirect.addFlashAttribute("msgErroExtra", List.of("Peça inválida selecionada."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        peca.setNomePeca(nv.nome);
        peca.setValor(nv.valor);
        peca.setIdReferencia(idReferencia);

        recalcularOrcamento(servico);
        servicoService.salvar(servico);

        redirect.addFlashAttribute("msg", "Peça extra atualizada com sucesso!");
        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    @PostMapping("/excluir-peca-extra")
    public String excluirPecaExtra(
            @RequestParam int idServico,
            @RequestParam int idPeca,
            RedirectAttributes redirect) {

        Servico servico = servicoService.buscarPorId(idServico);

        ServicoPeca peca = buscarPecaExtra(servico, idPeca);

        if (peca == null) {
            redirect.addFlashAttribute("msgErroExtra", List.of("Peça extra não encontrada."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        servico.removerPeca(peca);

        recalcularOrcamento(servico);
        servicoService.salvar(servico);

        redirect.addFlashAttribute("msg", "Peça extra removida com sucesso!");
        return "redirect:/statusdeservico/detalhes/" + idServico;
    }

    @PostMapping("/encerrar")
    public String encerrar(@RequestParam int idServico) {

        Servico servico = servicoService.buscarPorId(idServico);
        servico.setStatus(StatusServico.ENCERRADO);

        servicoService.salvar(servico);

        return "redirect:/statusdeservico";
    }

    private Integer getAtual(Servico servico, String tipo) {
        for (ServicoPeca p : servico.getPecas()) {
            if (tipo.equals(p.getTipo())) {
                return p.getIdReferencia();
            }
        }
        return null;
    }

    private ServicoPeca buscarPecaExtra(Servico servico, int idPeca) {
        for (ServicoPeca p : servico.getPecas()) {
            if (p.getTipo() == null && p.getId() == idPeca) {
                return p;
            }
        }
        return null;
    }

    private boolean existeCategoria(Servico servico, String categoria, Integer idExcluir) {
        for (ServicoPeca p : servico.getPecas()) {
            if (idExcluir != null && idExcluir.equals(p.getId())) {
                continue;
            }
            if (categoria.equals(p.getTipo())) {
                return true;
            }
            if (p.getTipo() == null && categoria.equals(p.getCategoriaExtra())) {
                return true;
            }
        }
        return false;
    }

    private String nomeAmigavel(String tipo) {
        return switch (tipo) {
            case "placa_mae" -> "Placa-mãe";
            case "processador" -> "Processador";
            case "memoria_ram" -> "Memória RAM";
            case "armazenamento" -> "Armazenamento";
            case "placa_devideo" -> "Placa de Vídeo";
            case "fonte" -> "Fonte";
            default -> tipo;
        };
    }

    private ResultadoCompatibilidade validarPecaExtra(
            Servico servico, CompatibilidadeData data, String categoria, int idReferencia) {

        PlacaMae placaMae = buscarPecaBase(servico, data, "placa_mae", PlacaMae.class);
        Processador processador = buscarPecaBase(servico, data, "processador", Processador.class);
        MemoriaRam ram = buscarPecaBase(servico, data, "memoria_ram", MemoriaRam.class);
        Armazenamento armazenamento = buscarPecaBase(servico, data, "armazenamento", Armazenamento.class);
        PlacadeVideo gpu = buscarPecaBase(servico, data, "placa_devideo", PlacadeVideo.class);
        Fonte fonte = buscarPecaBase(servico, data, "fonte", Fonte.class);

        switch (categoria) {
            case "placa_mae" -> placaMae = compatibilidadeService.buscarPorId(
                    data.getPlaca_mae(), p -> p.getId_placamae() == idReferencia);
            case "processador" -> processador = compatibilidadeService.buscarPorId(
                    data.getProcessador(), p -> p.getId_processador() == idReferencia);
            case "memoria_ram" -> ram = compatibilidadeService.buscarPorId(
                    data.getMemoria_ram(), m -> m.getId_memoriaRam() == idReferencia);
            case "armazenamento" -> armazenamento = compatibilidadeService.buscarPorId(
                    data.getArmazenamento(), a -> a.getId_armazenamento() == idReferencia);
            case "placa_devideo" -> gpu = compatibilidadeService.buscarPorId(
                    data.getPlaca_devideo(), v -> v.getId_placadevideo() == idReferencia);
            case "fonte" -> fonte = compatibilidadeService.buscarPorId(
                    data.getFonte(), f -> f.getId_fonte() == idReferencia);
            default -> {
                return null;
            }
        }

        if (placaMae == null || processador == null || ram == null
                || armazenamento == null || fonte == null) {
            return null;
        }

        return compatibilidadeService.verificarMontagemCompleta(
                placaMae, processador, ram, armazenamento, gpu, fonte);
    }

    @SuppressWarnings("unchecked")
    private <T> T buscarPecaBase(Servico servico, CompatibilidadeData data, String categoria, Class<T> classe) {
        Integer id = getAtual(servico, categoria);
        if (id == null) return null;

        return (T) switch (categoria) {
            case "placa_mae" -> compatibilidadeService.buscarPorId(
                    data.getPlaca_mae(), p -> p.getId_placamae() == id);
            case "processador" -> compatibilidadeService.buscarPorId(
                    data.getProcessador(), p -> p.getId_processador() == id);
            case "memoria_ram" -> compatibilidadeService.buscarPorId(
                    data.getMemoria_ram(), m -> m.getId_memoriaRam() == id);
            case "armazenamento" -> compatibilidadeService.buscarPorId(
                    data.getArmazenamento(), a -> a.getId_armazenamento() == id);
            case "placa_devideo" -> compatibilidadeService.buscarPorId(
                    data.getPlaca_devideo(), v -> v.getId_placadevideo() == id);
            case "fonte" -> compatibilidadeService.buscarPorId(
                    data.getFonte(), f -> f.getId_fonte() == id);
            default -> null;
        };
    }

    private void adicionarPeca(Servico servico, String tipo, int idReferencia, CompatibilidadeData data) {
        NomeValor nv = montarNomeValor(tipo, idReferencia, data, false);
        if (nv == null) return;

        ServicoPeca nova = new ServicoPeca();
        nova.setServico(servico);
        nova.setNomePeca(nv.nome);
        nova.setValor(nv.valor);
        nova.setTipo(tipo);
        nova.setIdReferencia(idReferencia);

        servico.adicionarPeca(nova);
    }

    private NomeValor montarNomeValor(String tipo, int idReferencia, CompatibilidadeData data, boolean extra) {
        String nome = null;
        Double valor = null;

        switch (tipo) {
            case "placa_mae" -> {
                var p = compatibilidadeService.buscarPorId(data.getPlaca_mae(), x -> x.getId_placamae() == idReferencia);
                if (p != null) { nome = "Placa" + (extra ? " (extra)" : "") + ": " + p.getModelo(); valor = p.getPreco(); }
            }
            case "processador" -> {
                var p = compatibilidadeService.buscarPorId(data.getProcessador(), x -> x.getId_processador() == idReferencia);
                if (p != null) { nome = "CPU" + (extra ? " (extra)" : "") + ": " + p.getModelo(); valor = p.getPreco(); }
            }
            case "memoria_ram" -> {
                var p = compatibilidadeService.buscarPorId(data.getMemoria_ram(), x -> x.getId_memoriaRam() == idReferencia);
                if (p != null) { nome = "RAM" + (extra ? " (extra)" : "") + ": " + p.getModelo(); valor = p.getPreco(); }
            }
            case "armazenamento" -> {
                var p = compatibilidadeService.buscarPorId(data.getArmazenamento(), x -> x.getId_armazenamento() == idReferencia);
                if (p != null) { nome = "Armazenamento" + (extra ? " (extra)" : "") + ": " + p.getModelo(); valor = p.getPreco(); }
            }
            case "placa_devideo" -> {
                var p = compatibilidadeService.buscarPorId(data.getPlaca_devideo(), x -> x.getId_placadevideo() == idReferencia);
                if (p != null) { nome = "GPU" + (extra ? " (extra)" : "") + ": " + p.getModelo(); valor = p.getPreco(); }
            }
            case "fonte" -> {
                var p = compatibilidadeService.buscarPorId(data.getFonte(), x -> x.getId_fonte() == idReferencia);
                if (p != null) { nome = "Fonte" + (extra ? " (extra)" : "") + ": " + p.getModelo(); valor = p.getPreco(); }
            }
            default -> {
                return null;
            }
        }

        if (nome == null) return null;
        return new NomeValor(nome, valor);
    }

    private static class NomeValor {
        final String nome;
        final Double valor;
        NomeValor(String nome, Double valor) {
            this.nome = nome;
            this.valor = valor;
        }
    }

    private void recalcularOrcamento(Servico servico) {
        double total = servico.getOrcamentoInicial();

        for (ServicoPeca p : servico.getPecas()) {
            if (p.getTipo() == null && p.getValor() != null) {
                total += p.getValor();
            }
        }

        servico.setOrcamentoFinal(total);
    }
}