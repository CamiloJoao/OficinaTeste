package com.projeto.oficina.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projeto.oficina.compatibilidade.*;
import com.projeto.oficina.enums.StatusServico;
import com.projeto.oficina.enums.TipoServico;
import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.model.Servico;
import com.projeto.oficina.model.ServicoPeca;
import com.projeto.oficina.service.ClienteService;
import com.projeto.oficina.service.ServicoService;

@Controller
public class MontagemController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private CompatibilidadeService compatibilidadeService;

    @GetMapping("/montagem")
    public String montagem(Model model) {
        adicionarCatalogo(model);
        model.addAttribute("pagina", "montagem");
        return "layout";
    }

    @PostMapping("/montagem/verificar")
    public String verificarCompatibilidade(
            @RequestParam String emailCliente,
            @RequestParam("placaMae") int idPlacaMae,
            @RequestParam("processador") int idProcessador,
            @RequestParam("memoriaRam") int idRam,
            @RequestParam("armazenamento") int idArmazenamento,
            @RequestParam(value = "placaDeVideo", required = false) Integer idPlacaDeVideo,
            @RequestParam("fonte") int idFonte,
            @RequestParam String dataPrevista,
            @RequestParam Double orcamento,
            Model model) {

        model.addAttribute("pagina", "montagem");
        adicionarCatalogo(model);

        if (emailCliente.trim().isEmpty()) {
            model.addAttribute("erro", "Digite o email do cliente.");
            return "layout";
        }

        Cliente cliente = clienteService.buscarPorEmail(emailCliente);

        if (cliente == null) {
            model.addAttribute("erro", "E-mail não cadastrado.");
            return "layout";
        }

        LocalDate dataConvertida;
        try {
            dataConvertida = LocalDate.parse(dataPrevista);
        } catch (Exception e) {
            model.addAttribute("erro", "Data inválida.");
            return "layout";
        }

        // Oficina fechada no domingo
        if (dataConvertida.getDayOfWeek() == DayOfWeek.SUNDAY) {
            model.addAttribute("erro", "A oficina não funciona aos domingos. Escolha outra data.");
            return "layout";
        }

        PlacaMae placaMae = compatibilidadeService.buscarPlacaMae(idPlacaMae);
        Processador processador = compatibilidadeService.buscarProcessador(idProcessador);
        MemoriaRam memoriaRam = compatibilidadeService.buscarMemoriaRam(idRam);
        Armazenamento armazenamento = compatibilidadeService.buscarArmazenamento(idArmazenamento);
        Fonte fonte = compatibilidadeService.buscarFonte(idFonte);
        PlacadeVideo placaDeVideo = compatibilidadeService.buscarPlacaDeVideo(idPlacaDeVideo);

        if (placaMae == null || processador == null || memoriaRam == null
                || armazenamento == null || fonte == null) {
            model.addAttribute("erro", "Selecione todas as peças corretamente.");
            return "layout";
        }

        ResultadoCompatibilidade resultado = compatibilidadeService.verificarMontagemCompleta(
                placaMae, processador, memoriaRam, armazenamento, placaDeVideo, fonte);

        if (!resultado.isCompativel()) {
            model.addAttribute("erroCompatibilidade", resultado.getErros());
            return "layout";
        }

        model.addAttribute("compativel", true);
        model.addAttribute("emailCliente", emailCliente);
        model.addAttribute("dataPrevista", dataPrevista);
        model.addAttribute("orcamento", orcamento);

        model.addAttribute("idPlacaMae", idPlacaMae);
        model.addAttribute("idProcessador", idProcessador);
        model.addAttribute("idRam", idRam);
        model.addAttribute("idArmazenamento", idArmazenamento);
        model.addAttribute("idPlacaDeVideo", idPlacaDeVideo);
        model.addAttribute("idFonte", idFonte);

        return "layout";
    }

    @PostMapping("/montagem/salvar")
    public String salvarMontagem(
            @RequestParam String emailCliente,
            @RequestParam String dataPrevista,
            @RequestParam Double orcamento,
            @RequestParam Integer idPlacaMae,
            @RequestParam Integer idProcessador,
            @RequestParam Integer idRam,
            @RequestParam Integer idArmazenamento,
            @RequestParam(required = false) Integer idPlacaDeVideo,
            @RequestParam Integer idFonte) {

        Cliente cliente = clienteService.buscarPorEmail(emailCliente);

        if (cliente == null) {
            return "redirect:/montagem";
        }

        PlacaMae placaMae = compatibilidadeService.buscarPlacaMae(idPlacaMae);
        Processador processador = compatibilidadeService.buscarProcessador(idProcessador);
        MemoriaRam memoriaRam = compatibilidadeService.buscarMemoriaRam(idRam);
        Armazenamento armazenamento = compatibilidadeService.buscarArmazenamento(idArmazenamento);
        Fonte fonte = compatibilidadeService.buscarFonte(idFonte);
        PlacadeVideo placaDeVideo = compatibilidadeService.buscarPlacaDeVideo(idPlacaDeVideo);

        Servico servico = new Servico();
        servico.setCliente(cliente);
        servico.setTipoServico(TipoServico.MONTAGEM);

        servico.adicionarPeca(criarPeca("Placa: " + placaMae.getModelo(), "placa_mae", idPlacaMae));
        servico.adicionarPeca(criarPeca("CPU: " + processador.getModelo(), "processador", idProcessador));
        servico.adicionarPeca(criarPeca("RAM: " + memoriaRam.getModelo(), "memoria_ram", idRam));
        servico.adicionarPeca(criarPeca("Armazenamento: " + armazenamento.getModelo(), "armazenamento", idArmazenamento));
        servico.adicionarPeca(criarPeca("Fonte: " + fonte.getModelo(), "fonte", idFonte));
        if (placaDeVideo != null) {
            servico.adicionarPeca(criarPeca("GPU: " + placaDeVideo.getModelo(), "placa_devideo", idPlacaDeVideo));
        }

        servico.setStatus(StatusServico.EM_ANDAMENTO);
        servico.setOrcamentoInicial(orcamento);
        servico.setOrcamentoFinal(orcamento);
        servico.setDataPrevistaConclusao(LocalDate.parse(dataPrevista));
        servico.setDataCadastro(LocalDate.now());

        servicoService.salvar(servico);

        return "redirect:/montagem?sucesso=true";
    }

    private void adicionarCatalogo(Model model) {
        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        model.addAttribute("placaMae", data.getPlaca_mae());
        model.addAttribute("processador", data.getProcessador());
        model.addAttribute("memoriaRam", data.getMemoria_ram());
        model.addAttribute("armazenamento", data.getArmazenamento());
        model.addAttribute("placaDeVideo", data.getPlaca_devideo());
        model.addAttribute("fonte", data.getFonte());
    }

    private ServicoPeca criarPeca(String nome, String tipo, Integer idReferencia) {
        ServicoPeca peca = new ServicoPeca();
        peca.setNomePeca(nome);
        peca.setValor(0.0);
        peca.setTipo(tipo);
        peca.setIdReferencia(idReferencia);
        return peca;
    }
}