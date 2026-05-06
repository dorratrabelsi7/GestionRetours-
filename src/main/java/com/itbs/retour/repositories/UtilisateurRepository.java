package com.itbs.retour.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.entities.Role;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    List<Utilisateur> findByRole(Role role);
    Utilisateur findByEmail(String email);
}