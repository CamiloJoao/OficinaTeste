package com.projeto.oficina.service;

import java.io.Serializable;

// Página usada pela pesquisa no Google (título e link), mostrada junto da sugestão
public class FonteDiagnostico implements Serializable {

    private String titulo;
    private String url;

    public FonteDiagnostico(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }
}
