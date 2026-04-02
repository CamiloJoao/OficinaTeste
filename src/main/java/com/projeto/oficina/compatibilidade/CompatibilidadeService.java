package com.projeto.oficina.compatibilidade;

import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
=======
import java.io.InputStream;

import org.springframework.stereotype.Service;
>>>>>>> 87ea526fe2591ec426152c378be484796860f804

@Service
public class CompatibilidadeService {

<<<<<<< HEAD
    private CompatibilidadeData data;

    // =========================
    // 🔹 CARREGAR JSON UMA VEZ
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

=======
    public CompatibilidadeData carregarCompatibilidade() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("regras/compatibilidade.json");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, CompatibilidadeData.class);
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar regras de compatibilidade", e);
        }
    }
<<<<<<< HEAD

    // =========================
    // 🔹 RETORNAR DADOS
    // =========================
    public CompatibilidadeData carregarCompatibilidade() {
        return data;
    }

    // =========================
    // 🔹 MÉTODO GENÉRICO (AQUI ESTÁ SUA DÚVIDA)
    // =========================
    public <T> T buscarPorId(List<T> lista, Predicate<T> filtro) {
        return lista.stream()
                .filter(filtro)
                .findFirst()
                .orElse(null);
    }
}
=======
}
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
