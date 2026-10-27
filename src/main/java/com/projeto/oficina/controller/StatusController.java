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

        PlacaMae placa = compatibilidadeService.buscarPlacaMae(placaMae);
        Processador proc = compatibilidadeService.buscarProcessador(processador);
        MemoriaRam ram = compatibilidadeService.buscarMemoriaRam(memoriaRam);
        Armazenamento arm = compatibilidadeService.buscarArmazenamento(armazenamento);
        Fonte fnt = compatibilidadeService.buscarFonte(fonte);
        PlacadeVideo gpu = compatibilidadeService.buscarPlacaDeVideo(placaDeVideo);

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

        adicionarPeca(servico, "placa_mae", placaMae);
        adicionarPeca(servico, "processador", processador);
        adicionarPeca(servico, "memoria_ram", memoriaRam);
        adicionarPeca(servico, "armazenamento", armazenamento);
        adicionarPeca(servico, "fonte", fonte);

        if (placaDeVideo != null) {
            adicionarPeca(servico, "placa_devideo", placaDeVideo);
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

        if (SINGULARES.contains(tipo) && existeCategoria(servico, tipo, null)) {
            redirect.addFlashAttribute("msgErroExtra",
                    List.of("Já existe uma peça do tipo '" + nomeAmigavel(tipo)
                            + "' nessa configuração. Edite a peça existente em vez de adicionar outra."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        ResultadoCompatibilidade compat = validarPecaExtra(servico, tipo, idReferencia);
        if (compat != null && !compat.isCompativel()) {
            redirect.addFlashAttribute("msgErroExtra", compat.getErros());
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        ServicoPeca nova = montarPeca(tipo, idReferencia, true);

        if (nova == null) {
            redirect.addFlashAttribute("msgErroExtra", List.of("Peça inválida selecionada."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        nova.setCategoriaExtra(tipo);
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

        ResultadoCompatibilidade compat = validarPecaExtra(servico, categoria, idReferencia);
        if (compat != null && !compat.isCompativel()) {
            redirect.addFlashAttribute("msgErroExtra", compat.getErros());
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        ServicoPeca dadosNovos = montarPeca(categoria, idReferencia, true);

        if (dadosNovos == null) {
            redirect.addFlashAttribute("msgErroExtra", List.of("Peça inválida selecionada."));
            return "redirect:/statusdeservico/detalhes/" + idServico;
        }

        peca.setNomePeca(dadosNovos.getNomePeca());
        peca.setValor(dadosNovos.getValor());
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

    private ResultadoCompatibilidade validarPecaExtra(Servico servico, String categoria, int idReferencia) {

        PlacaMae placaMae = compatibilidadeService.buscarPlacaMae(getAtual(servico, "placa_mae"));
        Processador processador = compatibilidadeService.buscarProcessador(getAtual(servico, "processador"));
        MemoriaRam ram = compatibilidadeService.buscarMemoriaRam(getAtual(servico, "memoria_ram"));
        Armazenamento armazenamento = compatibilidadeService.buscarArmazenamento(getAtual(servico, "armazenamento"));
        PlacadeVideo gpu = compatibilidadeService.buscarPlacaDeVideo(getAtual(servico, "placa_devideo"));
        Fonte fonte = compatibilidadeService.buscarFonte(getAtual(servico, "fonte"));

        switch (categoria) {
            case "placa_mae" -> placaMae = compatibilidadeService.buscarPlacaMae(idReferencia);
            case "processador" -> processador = compatibilidadeService.buscarProcessador(idReferencia);
            case "memoria_ram" -> ram = compatibilidadeService.buscarMemoriaRam(idReferencia);
            case "armazenamento" -> armazenamento = compatibilidadeService.buscarArmazenamento(idReferencia);
            case "placa_devideo" -> gpu = compatibilidadeService.buscarPlacaDeVideo(idReferencia);
            case "fonte" -> fonte = compatibilidadeService.buscarFonte(idReferencia);
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

    private void adicionarPeca(Servico servico, String tipo, int idReferencia) {
        ServicoPeca nova = montarPeca(tipo, idReferencia, false);
        if (nova == null) {
            return;
        }
        nova.setTipo(tipo);
        servico.adicionarPeca(nova);
    }

    // Monta a peça com nome, valor e id do catálogo; null se o id não existir
    private ServicoPeca montarPeca(String tipo, int idReferencia, boolean extra) {
        String rotulo = null;
        String modelo = null;
        Double valor = null;

        switch (tipo) {
            case "placa_mae" -> {
                PlacaMae placaMae = compatibilidadeService.buscarPlacaMae(idReferencia);
                if (placaMae != null) {
                    rotulo = "Placa";
                    modelo = placaMae.getModelo();
                    valor = placaMae.getPreco();
                }
            }
            case "processador" -> {
                Processador processador = compatibilidadeService.buscarProcessador(idReferencia);
                if (processador != null) {
                    rotulo = "CPU";
                    modelo = processador.getModelo();
                    valor = processador.getPreco();
                }
            }
            case "memoria_ram" -> {
                MemoriaRam ram = compatibilidadeService.buscarMemoriaRam(idReferencia);
                if (ram != null) {
                    rotulo = "RAM";
                    modelo = ram.getModelo();
                    valor = ram.getPreco();
                }
            }
            case "armazenamento" -> {
                Armazenamento armazenamento = compatibilidadeService.buscarArmazenamento(idReferencia);
                if (armazenamento != null) {
                    rotulo = "Armazenamento";
                    modelo = armazenamento.getModelo();
                    valor = armazenamento.getPreco();
                }
            }
            case "placa_devideo" -> {
                PlacadeVideo gpu = compatibilidadeService.buscarPlacaDeVideo(idReferencia);
                if (gpu != null) {
                    rotulo = "GPU";
                    modelo = gpu.getModelo();
                    valor = gpu.getPreco();
                }
            }
            case "fonte" -> {
                Fonte fonte = compatibilidadeService.buscarFonte(idReferencia);
                if (fonte != null) {
                    rotulo = "Fonte";
                    modelo = fonte.getModelo();
                    valor = fonte.getPreco();
                }
            }
        }

        if (rotulo == null) {
            return null;
        }
        if (extra) {
            rotulo += " (extra)";
        }

        ServicoPeca peca = new ServicoPeca();
        peca.setNomePeca(rotulo + ": " + modelo);
        peca.setValor(valor);
        peca.setIdReferencia(idReferencia);
        return peca;
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