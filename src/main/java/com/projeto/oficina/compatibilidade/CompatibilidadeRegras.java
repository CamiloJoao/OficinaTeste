package com.projeto.oficina.compatibilidade;

import org.springframework.stereotype.Component;

@Component
public class CompatibilidadeRegras {


    public boolean isCompativel(PlacaMae placamae, Processador processador, MemoriaRam ram, Armazenamento armazenamento) {
        boolean compatibilidadeSocket = placamae.getSoquetePlacaMae().equalsIgnoreCase(processador.getSoqueteProcessador());
        boolean compatibilidadeMemoria = placamae.getRam_suportada().equalsIgnoreCase(ram.getTipo());
        boolean compatibilidadeArmazenamento = placamae.getInterfaces_armazenamento().containsKey(armazenamento.getInterfaceConexao());
        return compatibilidadeSocket && compatibilidadeMemoria && compatibilidadeArmazenamento;
    }    
}
