package com.itbs.retour.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.entities.Role;
import com.itbs.retour.repositories.UtilisateurRepository;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    public List<Utilisateur> trouverTousLesUtilisateurs() {
        return utilisateurRepo.findAll();
    }

    // ✅ méthode manquante ajoutée
    public Utilisateur trouverParId(int id) {
        return utilisateurRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
    }

    public List<Utilisateur> trouverParRole(Role role) {
        return utilisateurRepo.findByRole(role);
    }

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        utilisateurRepo.save(utilisateur);
    }

    public ResponseEntity<String> supprimerUtilisateur(int id) {
        utilisateurRepo.findById(id).ifPresentOrElse(
            u -> { utilisateurRepo.delete(u); },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"); }
        );
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    public ResponseEntity<String> mettreAjourUtilisateur(int id, Utilisateur utilisateur) {
        utilisateurRepo.findById(id).ifPresentOrElse(
            u -> {
                u.setNom(utilisateur.getNom());
                u.setEmail(utilisateur.getEmail());
                u.setRole(utilisateur.getRole());
                utilisateurRepo.save(u);
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"); }
        );
        return ResponseEntity.ok("Utilisateur mis à jour avec succès");
    }
}