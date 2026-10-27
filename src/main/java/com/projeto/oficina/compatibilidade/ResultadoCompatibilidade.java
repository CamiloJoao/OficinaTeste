package com.projeto.oficina.compatibilidade;

import java.util.ArrayList;
import java.util.List;

public class ResultadoCompatibilidade {

    private List<String> erros = new ArrayList<>();

    public void adicionarErro(String mensagem) {
        this.erros.add(mensagem);
    }

    public boolean isCompativel() {
        return erros.isEmpty();
    }

    public List<String> getErros() {
        return erros;
    }
}
