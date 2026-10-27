package com.projeto.oficina.compatibilidade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

@Service
public class CompatibilidadeService {

    private CompatibilidadeData data;

    @Autowired
    private CompatibilidadeRegras regras;

    // Lê o compatibilidade.json uma vez, quando o sistema sobe
    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("regras/compatibilidade.json")) {

            if (is == null) {
                throw new RuntimeException("Arquivo compatibilidade.json não encontrado!");
            }

            ObjectMapper mapper = new ObjectMapper();
            this.data = mapper.readValue(is, CompatibilidadeData.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompatibilidadeData carregarCompatibilidade() {
        return data;
    }

    public <T> T buscarPorId(List<T> lista, Predicate<T> filtro) {
        return lista.stream()
                .filter(filtro)
                .findFirst()
                .orElse(null);
    }

    // Devolve a peça com esse id, ou null (id null = peça não escolhida, como a placa de vídeo)
    public PlacaMae buscarPlacaMae(Integer id) {
        if (id == null) {
            return null;
        }
        for (PlacaMae placaMae : data.getPlaca_mae()) {
            if (placaMae.getId_placamae() == id) {
                return placaMae;
            }
        }
        return null;
    }

    public Processador buscarProcessador(Integer id) {
        if (id == null) {
            return null;
        }
        for (Processador processador : data.getProcessador()) {
            if (processador.getId_processador() == id) {
                return processador;
            }
        }
        return null;
    }

    public MemoriaRam buscarMemoriaRam(Integer id) {
        if (id == null) {
            return null;
        }
        for (MemoriaRam memoriaRam : data.getMemoria_ram()) {
            if (memoriaRam.getId_memoriaRam() == id) {
                return memoriaRam;
            }
        }
        return null;
    }

    public Armazenamento buscarArmazenamento(Integer id) {
        if (id == null) {
            return null;
        }
        for (Armazenamento armazenamento : data.getArmazenamento()) {
            if (armazenamento.getId_armazenamento() == id) {
                return armazenamento;
            }
        }
        return null;
    }

    public PlacadeVideo buscarPlacaDeVideo(Integer id) {
        if (id == null) {
            return null;
        }
        for (PlacadeVideo placaDeVideo : data.getPlaca_devideo()) {
            if (placaDeVideo.getId_placadevideo() == id) {
                return placaDeVideo;
            }
        }
        return null;
    }

    public Fonte buscarFonte(Integer id) {
        if (id == null) {
            return null;
        }
        for (Fonte fonte : data.getFonte()) {
            if (fonte.getId_fonte() == id) {
                return fonte;
            }
        }
        return null;
    }

    public ResultadoCompatibilidade verificarMontagemCompleta(
            PlacaMae placaMae,
            Processador processador,
            MemoriaRam ram,
            Armazenamento armazenamento,
            PlacadeVideo placaDeVideo, // pode ser null (vídeo integrado)
            Fonte fonte) {

        ResultadoCompatibilidade resultado = new ResultadoCompatibilidade();

        // 1. Núcleo: placa mãe + processador + ram + armazenamento
        if (!regras.isCompativelSocket(placaMae, processador)) {
            resultado.adicionarErro("Soquete do processador (" + processador.getSoqueteProcessador()
                    + ") incompatível com a placa mãe (" + placaMae.getSoquetePlacaMae() + ").");
        }

        if (!regras.isCompativelMemoria(placaMae, ram)) {
            resultado.adicionarErro("Tipo de memória RAM (" + ram.getTipo()
                    + ") incompatível com a placa mãe (" + placaMae.getRam_suportada() + ").");
        }

        if (!regras.isCompativelArmazenamento(placaMae, armazenamento)) {
            resultado.adicionarErro("Interface de armazenamento (" + armazenamento.getInterfaceConexao()
                    + ") não suportada pela placa mãe.");
        }

        // 2. Placa de vídeo (se houver) ou vídeo integrado
        if (placaDeVideo != null) {
            if (!regras.isCompativelGPU(placaMae, placaDeVideo)) {
                resultado.adicionarErro("Interface da placa de vídeo (" + placaDeVideo.getInterfacePcie()
                        + ") incompatível com o slot da placa mãe (" + placaMae.getInterfacePcie() + ").");
            }
        } else {
            if (!regras.temVideoIntegrado(placaMae, processador)) {
                resultado.adicionarErro("Nenhuma placa de vídeo informada e nem processador/placa mãe possuem vídeo integrado.");
            }
        }

        // 3. Fonte: precisa suportar o consumo total
        if (!regras.isFonteSuficiente(fonte, processador, placaDeVideo)) {
            resultado.adicionarErro("Potência da fonte (" + fonte.getPotenciaWatts()
                    + "W) insuficiente para o consumo estimado do sistema (com margem de segurança de 20%).");
        }

        return resultado;
    }
}