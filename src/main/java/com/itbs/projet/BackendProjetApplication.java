package com.itbs.projet;

import org.springframework.boot.SpringApplication;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;


@SpringBootApplication
@ComponentScan(basePackages = {"com.itbs.retour", "com.itbs.projet"})
public class BackendProjetApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendProjetApplication.class, args);
	}

	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins("http://localhost:4200", "http://localhost:3000", "*")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(false)
						.maxAge(3600);
			}
		};
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("API Gestion des Retours")
				.version("1.0.0")
				.description("API REST pour le système de gestion des retours produits")
				.contact(new Contact()
					.name("Support")
					.email("support@gestionretours.com")));
	}
}

