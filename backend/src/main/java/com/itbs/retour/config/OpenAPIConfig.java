package com.itbs.retour.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion des Retours Produits")
                        .version("1.0.0")
                        .description("API REST pour la gestion des retours produits et non-conformités")
                        .contact(new Contact()
                                .name("Support Technique")
                                .email("support@gestion-retours.com")
                                .url("https://gestion-retours.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addServersItem(new Server()
                        .url("/api")
                        .description("Serveur local"))
                .addServersItem(new Server()
                        .url("https://api.gestion-retours.com")
                        .description("Serveur production"));
    }
}
