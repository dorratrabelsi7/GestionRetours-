package com.itbs.retour.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.itbs.retour.entities.Role;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.repositories.UtilisateurRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UtilisateurRepository utilisateurRepo) {
        return args -> {
            Utilisateur admin = utilisateurRepo.findByEmailIgnoreCase("admin@gestion-retours.com")
                .orElseGet(() -> utilisateurRepo.findFirstByRole(Role.ADMIN).orElseGet(Utilisateur::new));
            admin.setNom("Administrateur");
            admin.setEmail("admin@gestion-retours.com");
            admin.setMotDePasse("admin123");
            admin.setRole(Role.ADMIN);
            utilisateurRepo.save(admin);
        };
    }
}
