package com.projeto.oficina.compatibilidade;

import org.springframework.stereotype.Component;

@Component
public class CompatibilidadeRegras {

    private static final double MARGEM_SEGURANCA = 1.20; // 20% de folga

    // =========================
    // REGRAS INDIVIDUAIS
    // =========================

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
        return processador.isvideo_integrado()
                || (placaMae.getVideo_integrado() != null && placaMae.getVideo_integrado());
    }

    public boolean isFonteSuficiente(Fonte fonte, Processador processador, PlacadeVideo placaDeVideo) {
        int consumoProcessador = processador.getConsumoWatts() != null ? processador.getConsumoWatts() : 0;
        int consumoGPU = (placaDeVideo != null && placaDeVideo.getConsumoWatts() != null) ? placaDeVideo.getConsumoWatts() : 0;

        double consumoTotalComMargem = (consumoProcessador + consumoGPU) * MARGEM_SEGURANCA;

        return fonte.getPotenciaWatts() != null && fonte.getPotenciaWatts() >= consumoTotalComMargem;
    }

    // =========================
    // REGRA COMPOSTA (núcleo original, mantida por compatibilidade)
    // =========================

    public boolean isCompativel(PlacaMae placamae, Processador processador, MemoriaRam ram, Armazenamento armazenamento) {
        return isCompativelSocket(placamae, processador)
                && isCompativelMemoria(placamae, ram)
                && isCompativelArmazenamento(placamae, armazenamento);
    }
}