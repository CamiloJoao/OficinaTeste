package com.projeto.oficina.compatibilidade;

import org.springframework.stereotype.Component;

@Component
public class CompatibilidadeRegras {

    public boolean isCompativel(PlacaMae placa, Processador processador, MemoriaRam ram) {
        boolean compatibilidadeSocket = placa.getSoquete().equalsIgnoreCase(processador.getSoquete());
        boolean compatibilidadeMemoria = placa.getRam_suportada().equalsIgnoreCase(ram.getTipo());
        return compatibilidadeSocket && compatibilidadeMemoria;
    }
}
