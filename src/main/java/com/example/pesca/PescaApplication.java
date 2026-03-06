package com.example.pesca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PescaApplication {

	public static void main(String[] args) {
		// Carrega o .env ANTES do Spring iniciar
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Injeta as variáveis como System properties para o Spring conseguir ler
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
		SpringApplication.run(PescaApplication.class, args);
	}

}
