package com.itbs.retour.services;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.RetourProduit;
import com.itbs.retour.entities.EtatTraitement;
import com.itbs.retour.repositories.RetourProduitRepository;

@Service
public class RetourProduitService {

    @Autowired
    private RetourProduitRepository retourRepo;

    public List<RetourProduit> trouverTousLesRetours() {
        return retourRepo.findAll();
    }

    public RetourProduit trouverRetourParId(int id) {
        return retourRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Retour non trouvé"));
    }

    public List<RetourProduit> trouverParEtat(EtatTraitement etat) {
        return retourRepo.findByEtatTraitement(etat);
    }

    public List<RetourProduit> trouverParClient(int clientId) {
        return retourRepo.findByClientId(clientId);
    }

    public List<RetourProduit> trouverParPeriode(Date dateDebut, Date dateFin) {
        return retourRepo.findByDateBetween(dateDebut, dateFin);
    }

    public void enregistrerRetour(RetourProduit retour) {
        retourRepo.save(retour);
    }

    public ResponseEntity<String> supprimerRetour(int id) {
        retourRepo.findById(id).ifPresentOrElse(
            r -> { retourRepo.delete(r); },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Retour non trouvé"); }
        );
        return ResponseEntity.ok("Retour supprimé avec succès");
    }

    public ResponseEntity<String> mettreAjourRetour(int id, RetourProduit retour) {
        retourRepo.findById(id).ifPresentOrElse(
            r -> {
                r.setProduit(retour.getProduit());
                r.setRaison(retour.getRaison());
                r.setEtatTraitement(retour.getEtatTraitement());
                r.setDate(retour.getDate());
                retourRepo.save(r);
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Retour non trouvé"); }
        );
        return ResponseEntity.ok("Retour mis à jour avec succès");
    }

    public ResponseEntity<String> validerRetour(int id) {
        retourRepo.findById(id).ifPresentOrElse(
            r -> {
                r.setEtatTraitement(EtatTraitement.VALIDE);
                retourRepo.save(r);
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Retour non trouvé"); }
        );
        return ResponseEntity.ok("Retour validé avec succès");
    }

    public ResponseEntity<String> rejeterRetour(int id) {
        retourRepo.findById(id).ifPresentOrElse(
            r -> {
                r.setEtatTraitement(EtatTraitement.REJETE);
                retourRepo.save(r);
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Retour non trouvé"); }
        );
        return ResponseEntity.ok("Retour rejeté");
    }
}