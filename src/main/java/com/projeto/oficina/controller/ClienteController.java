package com.projeto.oficina.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.oficina.model.Cliente;
import com.projeto.oficina.service.ClienteService;
import com.projeto.oficina.service.ServicoService;

@Controller
@RequestMapping("/gerenciarclientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicoService servicoService;

    @GetMapping
    public String listarClientes(
        @RequestParam(required = false) String nome,
        @RequestParam(defaultValue = "false") boolean mostrarTodos,
        Model model) {

        model.addAttribute("pagina", "gerenciarclientes");

        String busca = "";
        if (nome != null) {
            busca = nome.trim();
        }

        if (busca.isEmpty()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("mostrarTodos", mostrarTodos);
        } else if (!busca.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            model.addAttribute("erroBusca", "Digite um nome válido.");
            model.addAttribute("clientes", List.of());
        } else {
            model.addAttribute("clientes", clienteService.buscarPorNome(busca));
            model.addAttribute("mostrarTodos", true); // busca mostra todos
        }

        return "layout";
    }

    @GetMapping("/cadastrar")
    public String novoCliente(Model model) {
        model.addAttribute("pagina", "cadastrocliente");
        return "layout";
    }

    @PostMapping("/salvar")
    public String salvarCliente(Cliente cliente, Model model) {

        model.addAttribute("pagina", "cadastrocliente");

        try {
            clienteService.salvar(cliente);
            model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("erro", "Email já cadastrado.");
        }

        return "layout";
    }

    @PostMapping("/excluir")
    public String excluir(@RequestParam Integer idCliente, RedirectAttributes redirect) {

        Cliente cliente = clienteService.buscarPorId(idCliente);

        if (cliente == null) {
            redirect.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/gerenciarclientes";
        }

        // Cliente com serviços fica, para não perder o histórico da oficina (RF10)
        if (!servicoService.buscarPorCliente(cliente).isEmpty()) {
            redirect.addFlashAttribute("erro", "Este cliente tem serviços cadastrados e não pode ser excluído.");
            return "redirect:/gerenciarclientes";
        }

        clienteService.excluir(idCliente);

        redirect.addFlashAttribute("mensagem", "Cliente excluído com sucesso!");
        return "redirect:/gerenciarclientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable int id, Model model) {

        Cliente cliente = clienteService.buscarPorId(id);

        model.addAttribute("cliente", cliente);
        model.addAttribute("pagina", "editarclientes");

        return "layout";
    }

    @PostMapping("/atualizar")
    public String atualizar(Cliente cliente) {
        clienteService.salvar(cliente);
        return "redirect:/gerenciarclientes";
    }
}
