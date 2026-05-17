package com.itbs.retour.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.EtatTraitement;
import com.itbs.retour.entities.RetourProduit;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.entities.Role;
import com.itbs.retour.repositories.RetourProduitRepository;
import com.itbs.retour.repositories.UtilisateurRepository;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private RetourProduitRepository retourRepo;

    public List<Utilisateur> trouverTousLesUtilisateurs() {
        return utilisateurRepo.findAll();
    }

    public Utilisateur trouverParId(int id) {
        return utilisateurRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id : " + id));
    }

    public List<Utilisateur> trouverParRole(Role role) {
        return utilisateurRepo.findByRole(role);
    }

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        utilisateurRepo.findByEmailIgnoreCase(utilisateur.getEmail()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email existe deja");
        });
        if (utilisateur.getRole() == Role.ADMIN && utilisateurRepo.existsByRole(Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un administrateur existe deja");
        }
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse("password");
        }
        utilisateurRepo.save(utilisateur);
    }

    public ResponseEntity<String> supprimerUtilisateur(int id) {
        utilisateurRepo.findById(id).ifPresentOrElse(
            u -> {
                for (RetourProduit retour : retourRepo.findByClientId(id)) {
                    if (retour.getEtatTraitement() == EtatTraitement.EN_ATTENTE || retour.getEtatTraitement() == EtatTraitement.EN_COURS) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible de supprimer un utilisateur avec des retours actifs");
                    }
                }
                utilisateurRepo.delete(u);
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id : " + id); }
        );
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    public ResponseEntity<String> mettreAjourUtilisateur(int id, Utilisateur utilisateur) {
        utilisateurRepo.findById(id).ifPresentOrElse(
            u -> {
                u.setNom(utilisateur.getNom());
                if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isBlank()) {
                    u.setMotDePasse(utilisateur.getMotDePasse());
                }
                if (utilisateur.getPhoto() != null && !utilisateur.getPhoto().isBlank()) {
                    u.setPhoto(utilisateur.getPhoto());
                }
                u.setRole(utilisateur.getRole());
                utilisateurRepo.save(u);
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id : " + id); }
        );
        return ResponseEntity.ok("Utilisateur mis à jour avec succès");
    }
}
