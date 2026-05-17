package com.itbs.retour.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.PasswordResetToken;
import com.itbs.retour.entities.Utilisateur;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUtilisateurAndUsedFalse(Utilisateur utilisateur);
}
