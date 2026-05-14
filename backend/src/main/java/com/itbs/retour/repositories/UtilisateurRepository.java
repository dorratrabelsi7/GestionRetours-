package com.itbs.retour.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.Role;
import com.itbs.retour.entities.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    List<Utilisateur> findByRole(Role role);
    Optional<Utilisateur> findFirstByRole(Role role);
    Optional<Utilisateur> findByEmailIgnoreCase(String email);
    boolean existsByRole(Role role);
}
