package com.projeto.oficina;

<<<<<<< HEAD
import org.hibernate.boot.model.source.spi.SizeSource;
=======
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.projeto.oficina.compatibilidade.CompatibilidadeService;
import com.projeto.oficina.compatibilidade.CompatibilidadeRegras;
import com.projeto.oficina.compatibilidade.PlacaMae;
import com.projeto.oficina.compatibilidade.Processador;
import com.projeto.oficina.compatibilidade.MemoriaRam;
<<<<<<< HEAD
import com.projeto.oficina.compatibilidade.Armazenamento;
=======
>>>>>>> 87ea526fe2591ec426152c378be484796860f804

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
<<<<<<< HEAD
		PlacaMae placa = compatibilidade.getPlaca_mae().get(1);
		Processador processador = compatibilidade.getProcessador().get(1);
		MemoriaRam ram = compatibilidade.getMemoria_ram().get(1);
		Armazenamento armazenamento = compatibilidade.getArmazenamento().get(1);

		// Testa compatibilidade
		boolean resultado = compatRegras.isCompativel(placa, processador, ram, armazenamento);
=======
		PlacaMae placa = compatibilidade.getPlacas_mae().get(1);
		Processador processador = compatibilidade.getProcessadores().get(0);
		MemoriaRam ram = compatibilidade.getMemorias_ram().get(0);

		// Testa compatibilidade
		boolean resultado = compatRegras.isCompativel(placa, processador, ram);
>>>>>>> 87ea526fe2591ec426152c378be484796860f804

		// Exibe o resultado
		System.out.println("Compatibilidade entre peças:");
		System.out.println("Placa-mãe: " + placa.getModelo());
		System.out.println("Processador: " + processador.getModelo());
		System.out.println("Memória RAM: " + ram.getTipo());
<<<<<<< HEAD
		System.out.println("Armazenamento: " + armazenamento.getTipo() + " -- Modelo: " + armazenamento.getModelo());
=======
>>>>>>> 87ea526fe2591ec426152c378be484796860f804
		System.out.println("Resultado: " + (resultado ? "Compatível " : "Incompatível "));
	}
}

