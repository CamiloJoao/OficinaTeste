package com.projeto.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.projeto.oficina.service.ClienteService;
import com.projeto.oficina.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Controller
@RequestMapping("/gerenciarclientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // 🔹 Tela principal (gerenciar clientes)
    @GetMapping
    public String listarClientes(@RequestParam(required = false) String nome, Model model) {

        List<Cliente> clientes;

        if (nome != null && !nome.isEmpty()) {
            clientes = clienteService.buscarPorNome(nome);
        } else {
            clientes = clienteService.listarTodos();
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("pagina", "gerenciarclientes");

        return "layout";
    }

    // Adicionar novo cadastro de cliente
    @GetMapping("/cadastrar")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("pagina", "cadastrocliente");
        return "layout";
    }

    // 🔹 Salvar novo cliente
    @PostMapping("/salvar")
    public String salvarCliente(Cliente cliente) {
        clienteService.salvar(cliente);
        return "redirect:/gerenciarclientes";
    }

    // 🔹 Excluir cliente
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        clienteService.excluir(id);
        return "redirect:/gerenciarclientes";
    }

    // 🔹 Buscar cliente para edição
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable int id, Model model) {

        Cliente cliente = clienteService.buscarPorId(id);

        model.addAttribute("cliente", cliente);
        model.addAttribute("pagina", "editarclientes");

        return "layout";
    }

    // 🔹 Atualizar cliente
    @PostMapping("/atualizar")
    public String atualizar(Cliente cliente) {
        clienteService.salvar(cliente);
        return "redirect:/gerenciarclientes";
    }
}
