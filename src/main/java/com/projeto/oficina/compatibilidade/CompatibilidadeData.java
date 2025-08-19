
package com.projeto.oficina.compatibilidade;

import java.util.List;

public class CompatibilidadeData {
    
    private List<PlacaMae> placas_mae;
    private List<Processador> processadores;
    private List<MemoriaRam> memorias_ram;

    public List<PlacaMae> getPlacas_mae() {
        return placas_mae;
    }

    public void setPlacas_mae(List<PlacaMae> placas_mae) {
        this.placas_mae = placas_mae;
    }

    public List<Processador> getProcessadores() {
        return processadores;
    }

    public void setProcessadores(List<Processador> processadores) {
        this.processadores = processadores;
    }

    public List<MemoriaRam> getMemorias_ram() {
        return memorias_ram;
    }

    public void setMemorias_ram(List<MemoriaRam> memorias_ram) {
        this.memorias_ram = memorias_ram;
    }
}

