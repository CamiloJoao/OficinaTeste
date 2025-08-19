package com.projeto.oficina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.projeto.oficina.compatibilidade.CompatibilidadeService;
import com.projeto.oficina.compatibilidade.CompatibilidadeRegras;
import com.projeto.oficina.compatibilidade.PlacaMae;
import com.projeto.oficina.compatibilidade.Processador;
import com.projeto.oficina.compatibilidade.MemoriaRam;

@SpringBootApplication
public class OficinaApplication {

	public static void main(String[] args) {
		// Inicia o Spring Boot e recupera o contexto
		ApplicationContext context = SpringApplication.run(OficinaApplication.class, args);

		// Obtém os beans de serviço
		CompatibilidadeService compatService = context.getBean(CompatibilidadeService.class);
		CompatibilidadeRegras compatRegras = context.getBean(CompatibilidadeRegras.class);

		// Carrega as listas do JSON
		var compatibilidade = compatService.carregarCompatibilidade();

		// Pega uma peça de cada lista (apenas para teste)
		PlacaMae placa = compatibilidade.getPlacas_mae().get(1);
		Processador processador = compatibilidade.getProcessadores().get(0);
		MemoriaRam ram = compatibilidade.getMemorias_ram().get(0);

		// Testa compatibilidade
		boolean resultado = compatRegras.isCompativel(placa, processador, ram);

		// Exibe o resultado
		System.out.println("Compatibilidade entre peças:");
		System.out.println("Placa-mãe: " + placa.getModelo());
		System.out.println("Processador: " + processador.getModelo());
		System.out.println("Memória RAM: " + ram.getTipo());
		System.out.println("Resultado: " + (resultado ? "Compatível " : "Incompatível "));
	}
}

