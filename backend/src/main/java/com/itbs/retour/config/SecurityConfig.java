package com.itbs.retour.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the Gestion des Retours API.
 * 
 * This config allows:
 * - OpenAPI documentation (/api/v3/api-docs, /swagger-ui.html, /swagger-ui/**)
 * - All REST API endpoints (/api/**)
 * 
 * CSRF is disabled for REST API convenience.
 * For production, implement proper authentication (JWT, OAuth2, etc.).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());
        
        return http.build();
    }
}
