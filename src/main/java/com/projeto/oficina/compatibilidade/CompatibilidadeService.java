package com.projeto.oficina.compatibilidade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

@Service
public class CompatibilidadeService {

    private CompatibilidadeData data;

    // =========================
    // CARREGAR JSON UMA VEZ
    // =========================
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

    // =========================
    // RETORNAR DADOS
    // =========================
    public CompatibilidadeData carregarCompatibilidade() {
        return data;
    }

    // =========================
    // MÉTODO GENÉRICO
    // =========================
    public <T> T buscarPorId(List<T> lista, Predicate<T> filtro) {
        return lista.stream()
                .filter(filtro)
                .findFirst()
                .orElse(null);
    }
}