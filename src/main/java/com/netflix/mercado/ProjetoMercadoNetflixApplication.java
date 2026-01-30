package com.netflix.mercado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do Netflix Mercados
 * 
 * Features:
 * - Autenticação JWT com refresh tokens
 * - Busca de mercados por geolocalização (Haversine)
 * - Sistema de avaliações e comentários
 * - Favoritos e notificações em tempo real (WebSocket)
 * - Promoções e descontos
 * - Auditoria e soft delete
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.netflix.mercado.repository")
@ComponentScan(basePackages = {
        "com.netflix.mercado.config",
        "com.netflix.mercado.controller",
        "com.netflix.mercado.service",
        "com.netflix.mercado.security"
})
@EnableAsync
@EnableScheduling
public class ProjetoMercadoNetflixApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetoMercadoNetflixApplication.class, args);
    }
}
