
package com.projeto.oficina.compatibilidade;

import java.util.List;

public class CompatibilidadeData {

    private List<PlacaMae> placa_mae;
    private List<Processador> processador;
    private List<MemoriaRam> memoria_ram;
    private List<Armazenamento> armazenamento;
    private List<PlacadeVideo> placa_devideo;
    private List<Fonte> fonte;

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

    public List<PlacadeVideo> getPlaca_devideo() {
        return placa_devideo;
    }

    public void setPlaca_devideo(List<PlacadeVideo> placa_devideo) {
        this.placa_devideo = placa_devideo;
    }

    public List<Fonte> getFonte() {
        return fonte;
    }

    public void setFonte(List<Fonte> fonte) {
        this.fonte = fonte;
    }
}

