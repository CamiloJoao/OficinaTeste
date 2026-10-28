package com.projeto.oficina.compatibilidade;

import org.springframework.stereotype.Component;

@Component
public class CompatibilidadeRegras {

    private static final double MARGEM_SEGURANCA = 1.20; // 20% de folga
    private static final int CONSUMO_DEMAIS_PECAS = 100; // estimativa em W para placa-mãe, memórias, armazenamento e ventoinhas

    public boolean isCompativelSocket(PlacaMae placaMae, Processador processador) {
        return placaMae.getSoquetePlacaMae().equalsIgnoreCase(processador.getSoqueteProcessador());
    }

    public boolean isCompativelMemoria(PlacaMae placaMae, MemoriaRam ram) {
        return placaMae.getRam_suportada().equalsIgnoreCase(ram.getTipo());
    }

    public boolean isCompativelArmazenamento(PlacaMae placaMae, Armazenamento armazenamento) {
        return placaMae.getInterfaces_armazenamento().containsKey(armazenamento.getInterfaceConexao());
    }

    public boolean isCompativelGPU(PlacaMae placaMae, PlacadeVideo placaDeVideo) {
        if (placaMae.getInterfacePcie() == null || placaDeVideo.getInterfacePcie() == null) {
            return false;
        }
        return placaMae.getInterfacePcie().equalsIgnoreCase(placaDeVideo.getInterfacePcie());
    }

    public boolean temVideoIntegrado(PlacaMae placaMae, Processador processador) {
        return processador.isVideo_integrado() || placaMae.isVideo_integrado();
    }

    public boolean isFonteSuficiente(Fonte fonte, Processador processador, PlacadeVideo placaDeVideo) {
        int consumoTotal = processador.getConsumoWatts() + CONSUMO_DEMAIS_PECAS;
        if (placaDeVideo != null) {
            consumoTotal += placaDeVideo.getConsumoWatts();
        }
        return fonte.getPotenciaWatts() >= consumoTotal * MARGEM_SEGURANCA;
    }
}