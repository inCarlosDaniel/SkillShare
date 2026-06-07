package com.SkillShare.SkillShare; 

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication; 
import org.springframework.scheduling.annotation.EnableAsync; 
import org.springframework.scheduling.annotation.EnableScheduling; 

@SpringBootApplication 
@EnableScheduling 
@EnableAsync 
public class SkillShareApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillShareApplication.class, args); // Inicializa o contexto Spring Boot, sobe o servidor embutido e registra todos os beans da aplicacao
	}

}
