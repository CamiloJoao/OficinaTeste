package com.projeto.oficina.compatibilidade;

import java.util.ArrayList;
import java.util.List;

public class ResultadoCompatibilidade {

    private boolean compativel;
    private List<String> erros = new ArrayList<>();

    public void adicionarErro(String mensagem) {
        this.erros.add(mensagem);
        this.compativel = false;
    }

    public ResultadoCompatibilidade() {
        this.compativel = true; // assume compatível até achar um erro
    }

    public boolean isCompativel() {
        return compativel;
    }

    public List<String> getErros() {
        return erros;
    }
}
