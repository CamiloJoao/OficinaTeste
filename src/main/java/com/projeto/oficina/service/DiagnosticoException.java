package com.projeto.oficina.service;

// Erro do apoio ao diagnóstico; a mensagem é a que aparece para o usuário
public class DiagnosticoException extends RuntimeException {

    public DiagnosticoException(String mensagem) {
        super(mensagem);
    }
}
