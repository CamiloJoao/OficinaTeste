package com.projeto.oficina.controller;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private CompatibilidadeRegras compatibilidadeRegras;

    
    // TELA INICIAL
    
   @GetMapping("/montagem")
        public String montagem(Model model) {

        CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

        model.addAttribute("placaMae", data.getPlaca_mae());
        model.addAttribute("processador", data.getProcessador());
        model.addAttribute("memoriaRam", data.getMemoria_ram());
        model.addAttribute("armazenamento", data.getArmazenamento());

        // 🔽 NOVO: buscar montagens
        List<Servico> montagens = servicoService.listarPorTipo(TipoServico.MONTAGEM);
        model.addAttribute("montagens", montagens);

        model.addAttribute("pagina", "montagem");
        return "layout";
        }
    
        
        // VERIFICAR COMPATIBILIDADE
       
      @PostMapping("/montagem/verificar")
        public String verificarCompatibilidade(
                @RequestParam String emailCliente,
                @RequestParam("placaMae") int idPlacaMae,
                @RequestParam("processador") int idProcessador,
                @RequestParam("memoriaRam") int idRam,
                @RequestParam("armazenamento") int idArmazenamento,
                @RequestParam String dataPrevista,
                @RequestParam Double orcamento,
                Model model) {

                CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

                model.addAttribute("placaMae", data.getPlaca_mae());
                model.addAttribute("processador", data.getProcessador());
                model.addAttribute("memoriaRam", data.getMemoria_ram());
                model.addAttribute("armazenamento", data.getArmazenamento());

                // VALIDAR EMAIL
                if (emailCliente == null || emailCliente.trim().isEmpty()) {
                        model.addAttribute("erro", "Digite o email do cliente.");
                        model.addAttribute("pagina", "montagem");
                        return "layout";
                }

                Cliente cliente = clienteService.buscarPorEmail(emailCliente);

                if (cliente == null) {
                        model.addAttribute("erro", "E-mail não cadastrado.");
                        model.addAttribute("pagina", "montagem");
                        return "layout";
                }

                // VALIDAR DATA
                try {
                        java.time.LocalDate.parse(dataPrevista);
                } catch (Exception e) {
                        model.addAttribute("erro", "Data inválida.");
                        model.addAttribute("pagina", "montagem");
                        return "layout";
                }

                // BUSCAR PEÇAS
                PlacaMae placaMae = compatibilidadeService.buscarPorId(
                        data.getPlaca_mae(), p -> p.getId_placamae() == idPlacaMae);

                Processador processador = compatibilidadeService.buscarPorId(
                        data.getProcessador(), p -> p.getId_processador() == idProcessador);

                MemoriaRam memoriaRam = compatibilidadeService.buscarPorId(
                        data.getMemoria_ram(), m -> m.getId_memoriaRam() == idRam);

                Armazenamento armazenamento = compatibilidadeService.buscarPorId(
                        data.getArmazenamento(), a -> a.getId_armazenamento() == idArmazenamento);

                if (placaMae == null || processador == null || memoriaRam == null || armazenamento == null) {
                        model.addAttribute("erro", "Selecione todas as peças corretamente.");
                        model.addAttribute("pagina", "montagem");
                        return "layout";
                }

                boolean compativel = compatibilidadeRegras.isCompativel(
                        placaMae, processador, memoriaRam, armazenamento);

                if (!compativel) {
                        model.addAttribute("erroCompatibilidade", "Peças incompatíveis ❌");
                        model.addAttribute("pagina", "montagem");
                        return "layout";
                }

                // CONFIRMAÇÃO
                model.addAttribute("compativel", true);
                model.addAttribute("emailCliente", emailCliente);
                model.addAttribute("dataPrevista", dataPrevista);
                model.addAttribute("orcamento", orcamento);

                
                model.addAttribute("idPlacaMae", idPlacaMae);
                model.addAttribute("idProcessador", idProcessador);
                model.addAttribute("idRam", idRam);
                model.addAttribute("idArmazenamento", idArmazenamento);

                model.addAttribute("pagina", "montagem");
                return "layout";
        }

        
        // SALVAR MONTAGEM
        
       @PostMapping("/montagem/salvar")
        public String salvarMontagem(
                @RequestParam String emailCliente,
                @RequestParam String dataPrevista,
                @RequestParam Double orcamento,
                @RequestParam Integer idPlacaMae,
                @RequestParam Integer idProcessador,
                @RequestParam Integer idRam,
                @RequestParam Integer idArmazenamento) {

                Cliente cliente = clienteService.buscarPorEmail(emailCliente);

                if (cliente == null) {
                        return "redirect:/montagem?erro=cliente";
                }

                // 🔥 CARREGAR DADOS
                CompatibilidadeData data = compatibilidadeService.carregarCompatibilidade();

                PlacaMae placaMae = compatibilidadeService.buscarPorId(
                        data.getPlaca_mae(), p -> p.getId_placamae() == idPlacaMae);

                Processador processador = compatibilidadeService.buscarPorId(
                        data.getProcessador(), p -> p.getId_processador() == idProcessador);

                MemoriaRam memoriaRam = compatibilidadeService.buscarPorId(
                        data.getMemoria_ram(), m -> m.getId_memoriaRam() == idRam);

                Armazenamento armazenamento = compatibilidadeService.buscarPorId(
                        data.getArmazenamento(), a -> a.getId_armazenamento() == idArmazenamento);


                Servico servico = new Servico();
                servico.setCliente(cliente);
                servico.setTipoServico(TipoServico.MONTAGEM);

                //SALVAR AS PECAS

                List<ServicoPeca> lista = new ArrayList<>();

                ServicoPeca p1 = new ServicoPeca();
                p1.setNomePeca("Placa: " + placaMae.getModelo());
                p1.setValor(0.0);
                p1.setServico(servico);
                p1.setTipo("placa_mae");
                p1.setIdReferencia(idPlacaMae);
                

                ServicoPeca p2 = new ServicoPeca();
                p2.setNomePeca("CPU: " + processador.getModelo());
                p2.setValor(0.0);
                p2.setServico(servico);
                p2.setTipo("processador");
                p2.setIdReferencia(idProcessador);

                ServicoPeca p3 = new ServicoPeca();
                p3.setNomePeca("RAM: " + memoriaRam.getModelo());
                p3.setValor(0.0);
                p3.setServico(servico);
                p3.setTipo("memoria_ram");
                p3.setIdReferencia(idRam);

                ServicoPeca p4 = new ServicoPeca();
                p4.setNomePeca("Armazenamento: " + armazenamento.getModelo());
                p4.setValor(0.0);
                p4.setServico(servico);
                p4.setTipo("armazenamento");
                p4.setIdReferencia(idArmazenamento);

                lista.add(p1);
                lista.add(p2);
                lista.add(p3);
                lista.add(p4);

                servico.setPecas(lista);

                servico.setStatus(StatusServico.EM_ANDAMENTO);
                servico.setOrcamentoInicial(orcamento);
                servico.setOrcamentoFinal(orcamento); 
                servico.setDataPrevistaConclusao(java.time.LocalDate.parse(dataPrevista));

                servicoService.salvar(servico);

                return "redirect:/montagem?sucesso=true";
        }
}

