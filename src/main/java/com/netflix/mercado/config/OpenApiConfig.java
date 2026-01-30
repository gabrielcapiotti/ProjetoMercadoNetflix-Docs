package com.netflix.mercado.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API
 * 
 * Disponível em:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/api/v3/api-docs
 * - OpenAPI YAML: http://localhost:8080/api/v3/api-docs.yaml
 * 
 * @author Netflix Mercados Team
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Netflix Mercados API}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configuração principal do OpenAPI
     */
    @Bean
    public OpenAPI netflixMercadosOpenAPI() {
        // Nome do esquema de segurança
        final String securitySchemeName = "bearer-jwt";

        return new OpenAPI()
                // Informações da API
                .info(apiInfo())
                
                // Servidores disponíveis
                .servers(List.of(
                    // Servidor local de desenvolvimento
                    new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Servidor de Desenvolvimento Local"),
                    
                    // Servidor de homologação
                    new Server()
                        .url("https://api-homolog.netflixmercados.com.br")
                        .description("Servidor de Homologação"),
                    
                    // Servidor de produção
                    new Server()
                        .url("https://api.netflixmercados.com.br")
                        .description("Servidor de Produção")
                ))
                
                // Componentes (Schemas de segurança)
                .components(new Components()
                    .addSecuritySchemes(securitySchemeName, securityScheme())
                )
                
                // Adiciona requisito de segurança global (JWT)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                
                // Tags para agrupar endpoints
                .tags(List.of(
                    new Tag()
                        .name("Autenticação")
                        .description("Endpoints de autenticação, login, registro e gerenciamento de tokens JWT"),
                    
                    new Tag()
                        .name("Mercados")
                        .description("CRUD e gerenciamento de mercados/lojas. Inclui listagem, busca, criação e atualização"),
                    
                    new Tag()
                        .name("Avaliações")
                        .description("Sistema de avaliações e ratings para mercados. Permite criar, editar e visualizar avaliações"),
                    
                    new Tag()
                        .name("Comentários")
                        .description("Sistema de comentários em mercados e produtos. Suporte para threads e respostas"),
                    
                    new Tag()
                        .name("Favoritos")
                        .description("Gerenciamento de mercados favoritos do usuário. Adicionar/remover favoritos e listar"),
                    
                    new Tag()
                        .name("Horários")
                        .description("Gerenciamento de horários de funcionamento dos mercados. CRUD completo de horários"),
                    
                    new Tag()
                        .name("Notificações")
                        .description("Sistema de notificações em tempo real. Push notifications e gerenciamento de preferências"),
                    
                    new Tag()
                        .name("Promoções")
                        .description("CRUD de promoções e ofertas especiais. Gerenciamento de campanhas promocionais")
                ));
    }

    /**
     * Informações detalhadas da API
     */
    private Info apiInfo() {
        return new Info()
                .title("Netflix Mercados API")
                .description(
                    "## API RESTful para Plataforma de Marketplaces\n\n" +
                    "Esta API fornece endpoints completos para gerenciamento de mercados, " +
                    "avaliações, comentários, favoritos, horários de funcionamento, notificações e promoções.\n\n" +
                    
                    "### Funcionalidades Principais\n" +
                    "- **Autenticação JWT**: Sistema completo de autenticação com refresh tokens\n" +
                    "- **Mercados**: CRUD completo de mercados/lojas com busca avançada\n" +
                    "- **Avaliações**: Sistema de ratings de 1 a 5 estrelas com validações\n" +
                    "- **Comentários**: Sistema de comentários com suporte a threads\n" +
                    "- **Favoritos**: Gerenciamento de mercados favoritos por usuário\n" +
                    "- **Horários**: Definição de horários de funcionamento por dia da semana\n" +
                    "- **Notificações**: Push notifications em tempo real\n" +
                    "- **Promoções**: Sistema de ofertas e campanhas promocionais\n\n" +
                    
                    "### Autenticação\n" +
                    "A maioria dos endpoints requer autenticação via JWT Bearer Token.\n" +
                    "1. Faça login em `/api/v1/auth/login`\n" +
                    "2. Copie o `accessToken` da resposta\n" +
                    "3. Clique no botão **Authorize** no topo da página\n" +
                    "4. Cole o token no campo e clique em **Authorize**\n\n" +
                    
                    "### Roles de Usuário\n" +
                    "- **CUSTOMER**: Cliente comum (pode avaliar, comentar, favoritar)\n" +
                    "- **SELLER**: Vendedor (pode criar e gerenciar mercados)\n" +
                    "- **ADMIN**: Administrador (acesso total ao sistema)\n\n" +
                    
                    "### Códigos de Status HTTP\n" +
                    "- **200 OK**: Requisição bem-sucedida\n" +
                    "- **201 Created**: Recurso criado com sucesso\n" +
                    "- **204 No Content**: Operação bem-sucedida sem corpo de resposta\n" +
                    "- **400 Bad Request**: Dados de entrada inválidos\n" +
                    "- **401 Unauthorized**: Token JWT ausente ou inválido\n" +
                    "- **403 Forbidden**: Usuário sem permissão para a operação\n" +
                    "- **404 Not Found**: Recurso não encontrado\n" +
                    "- **409 Conflict**: Conflito (ex: email já cadastrado)\n" +
                    "- **500 Internal Server Error**: Erro interno do servidor\n\n" +
                    
                    "### Paginação\n" +
                    "Endpoints de listagem suportam paginação via parâmetros:\n" +
                    "- `page`: Número da página (inicia em 0)\n" +
                    "- `size`: Quantidade de itens por página (padrão: 20)\n" +
                    "- `sort`: Campo e direção de ordenação (ex: `nome,asc`)\n\n" +
                    
                    "### Rate Limiting\n" +
                    "- **Limite**: 100 requisições por minuto por IP\n" +
                    "- **Header de resposta**: `X-RateLimit-Remaining`\n\n" +
                    
                    "### Versionamento\n" +
                    "A API utiliza versionamento via URL path: `/api/v1/`\n\n" +
                    
                    "### Suporte\n" +
                    "Para questões técnicas, entre em contato com a equipe de desenvolvimento."
                )
                .version(applicationVersion)
                .contact(new Contact()
                    .name("Netflix Mercados - Equipe de Desenvolvimento")
                    .email("dev@netflixmercados.com.br")
                    .url("https://netflixmercados.com.br/suporte")
                )
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                );
    }

    /**
     * Configuração do esquema de segurança JWT
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("bearer-jwt")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description(
                    "## Autenticação JWT (JSON Web Token)\n\n" +
                    
                    "### Como obter o token:\n" +
                    "1. Registre-se em **POST /api/v1/auth/register** ou\n" +
                    "2. Faça login em **POST /api/v1/auth/login**\n" +
                    "3. Copie o campo `accessToken` da resposta JSON\n\n" +
                    
                    "### Como usar o token:\n" +
                    "- Clique no botão **Authorize** (cadeado) no topo da página\n" +
                    "- Cole o token no campo de entrada\n" +
                    "- Clique em **Authorize**\n" +
                    "- O token será automaticamente incluído em todas as requisições\n\n" +
                    
                    "### Formato do Token:\n" +
                    "```\n" +
                    "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n" +
                    "```\n\n" +
                    
                    "### Expiração:\n" +
                    "- **Access Token**: 24 horas\n" +
                    "- **Refresh Token**: 7 dias\n\n" +
                    
                    "### Renovação:\n" +
                    "Use **POST /api/v1/auth/refresh** com o `refreshToken` para obter um novo `accessToken`"
                );
    }
}
