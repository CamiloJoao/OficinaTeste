package com.projeto.oficina.compatibilidade;

import org.springframework.stereotype.Component;

@Component
public class CompatibilidadeRegras {

<<<<<<< HEAD
    public boolean isCompativel(PlacaMae placamae, Processador processador, MemoriaRam ram, Armazenamento armazenamento) {
        boolean compatibilidadeSocket = placamae.getSoquetePlacaMae().equalsIgnoreCase(processador.getSoqueteProcessador());
        boolean compatibilidadeMemoria = placamae.getRam_suportada().equalsIgnoreCase(ram.getTipo());
        boolean compatibilidadeArmazenamento = placamae.getInterfaces_armazenamento().containsKey(armazenamento.getInterfaceConexao());
        return compatibilidadeSocket && compatibilidadeMemoria && compatibilidadeArmazenamento;
=======
    public boolean isCompativel(PlacaMae placa, Processador processador, MemoriaRam ram) {
        boolean compatibilidadeSocket = placa.getSoquete().equalsIgnoreCase(processador.getSoquete());
        boolean compatibilidadeMemoria = placa.getRam_suportada().equalsIgnoreCase(ram.getTipo());
        return compatibilidadeSocket && compatibilidadeMemoria;
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
    }
}
