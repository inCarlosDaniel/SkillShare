package com.SkillShare.SkillShare.config; 

import org.springframework.context.annotation.Bean; 

import org.springframework.context.annotation.Configuration; 

import org.springframework.core.task.TaskExecutor; 

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor; 

@Configuration 
public class AssincronoConfig {

    @Bean(name = "taskExecutor") 
    public TaskExecutor taskExecutor() { // Método que cria e configura o executor de tarefas assíncronas para envio de e-mails

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // Cria uma nova instância do executor baseado em pool de threads

        executor.setCorePoolSize(2); // Define 2 threads sempre ativas no pool, mesmo quando ociosas

        executor.setMaxPoolSize(10); // Define o número máximo de threads simultâneas que o pool pode criar sob carga

        executor.setQueueCapacity(50); // Define a capacidade da fila de espera para tarefas quando todas as threads estão ocupadas

        executor.setThreadNamePrefix("async-email-"); // Define o prefixo do nome das threads para facilitar a identificação em logs e profilers

        executor.initialize(); // Inicializa o pool de threads com as configurações definidas acima

        return executor; // Retorna o executor configurado para ser injetado onde necessário
    }
}
