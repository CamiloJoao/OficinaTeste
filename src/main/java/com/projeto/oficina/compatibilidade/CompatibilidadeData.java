
package com.projeto.oficina.compatibilidade;

import java.util.List;

public class CompatibilidadeData {
    

    private List<PlacaMae> placa_mae;
    private List<Processador> processador;
    private List<MemoriaRam> memoria_ram;
    private List<Armazenamento> armazenamento;


    public List<Armazenamento> getArmazenamento() {
        return armazenamento;
    }

    public void setArmazenamento(List<Armazenamento> armazenamento) {
        this.armazenamento = armazenamento;
    }

    public List<PlacaMae> getPlaca_mae() {
        return placa_mae;
    }

    public void setPlaca_mae(List<PlacaMae> placa_mae) {
        this.placa_mae = placa_mae;
    }

    public List<Processador> getProcessador() {
        return processador;
    }

    public void setProcessador(List<Processador> processador) {
        this.processador = processador;
    }

    public List<MemoriaRam> getMemoria_ram() {
        return memoria_ram;
    }

    public void setMemoria_ram(List<MemoriaRam> memoria_ram) {
        this.memoria_ram = memoria_ram;
    }
}

