package com.itbs.retour.services;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.HistoriqueRetour;
import com.itbs.retour.repositories.HistoriqueRetourRepository;

@Service
public class HistoriqueRetourService {

    @Autowired
    private HistoriqueRetourRepository historiqueRepo;

    public List<HistoriqueRetour> trouverTousLesHistoriques() {
        return historiqueRepo.findAll();
    }

    public HistoriqueRetour trouverParId(int id) {
        return historiqueRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Historique non trouvé"));
    }

    public List<HistoriqueRetour> trouverParRetour(int retourId) {
        return historiqueRepo.findByRetourId(retourId);
    }

    public List<HistoriqueRetour> trouverParEmploye(int employeId) {
        return historiqueRepo.findByEmployeId(employeId);
    }

    public List<HistoriqueRetour> trouverParPeriode(Date dateDebut, Date dateFin) {
        return historiqueRepo.findByDateBetween(dateDebut, dateFin);
    }

    public void enregistrerAction(HistoriqueRetour historique) {
        historiqueRepo.save(historique);
    }

    public ResponseEntity<String> supprimerHistorique(int id) {
        historiqueRepo.findById(id).ifPresentOrElse(
            h -> { historiqueRepo.delete(h); },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Historique non trouvé"); }
        );
        return ResponseEntity.ok("Historique supprimé avec succès");
    }
}