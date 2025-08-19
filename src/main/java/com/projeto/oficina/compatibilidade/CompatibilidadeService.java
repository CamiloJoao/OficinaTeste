package com.projeto.oficina.compatibilidade;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

import org.springframework.stereotype.Service;

@Service
public class CompatibilidadeService {

    public CompatibilidadeData carregarCompatibilidade() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("regras/compatibilidade.json");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, CompatibilidadeData.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar regras de compatibilidade", e);
        }
    }
}
