package com.projeto.oficina.service;

import java.io.Serializable;
import java.util.List;

// Resultado do apoio ao diagnóstico. É Serializable porque passa pela sessão no redirect.
public class SugestaoDiagnostico implements Serializable {

    private String texto;
    private List<FonteDiagnostico> fontes;

    // HTML pronto do Google com os termos pesquisados (null quando a pesquisa está desligada)
    private String sugestoesPesquisa;

    // Tempo que a IA levou para responder
    private long segundos;

    public SugestaoDiagnostico(String texto, List<FonteDiagnostico> fontes, String sugestoesPesquisa, long segundos) {
        this.texto = texto;
        this.fontes = fontes;
        this.sugestoesPesquisa = sugestoesPesquisa;
        this.segundos = segundos;
    }

    public String getTexto() {
        return texto;
    }

    public List<FonteDiagnostico> getFontes() {
        return fontes;
    }

    public String getSugestoesPesquisa() {
        return sugestoesPesquisa;
    }

    public long getSegundos() {
        return segundos;
    }
}
