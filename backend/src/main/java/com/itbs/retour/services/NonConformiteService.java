package com.itbs.retour.services;

import java.util.List;
import java.sql.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.NonConformite;
import com.itbs.retour.entities.Gravite;
import com.itbs.retour.entities.Role;
import com.itbs.retour.repositories.NonConformiteRepository;

@Service
public class NonConformiteService {

    @Autowired
    private NonConformiteRepository nonConformiteRepo;

    @Autowired
    private NotificationService notificationServ;

    public List<NonConformite> trouverToutesLesNonConformites() {
        return nonConformiteRepo.findAll();
    }

    public NonConformite trouverParId(int id) {
        return nonConformiteRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Non-conformité non trouvée avec l'id : " + id));
    }

    public List<NonConformite> trouverParGravite(Gravite gravite) {
        return nonConformiteRepo.findByGravite(gravite);
    }

    public List<NonConformite> trouverParRetour(int retourId) {
        return nonConformiteRepo.findByRetourId(retourId);
    }

    public NonConformite signalerNonConformite(NonConformite nonConformite) {
        nonConformite.setDate(new Date(System.currentTimeMillis()));
        NonConformite saved = nonConformiteRepo.save(nonConformite);
        if (saved.getGravite() == Gravite.CRITIQUE) {
            notificationServ.notifierRole(Role.QUALITE, "Non-conformite critique", "Une non-conformite critique a ete signalee");
            notificationServ.notifierAdmin("Non-conformite critique", "Une non-conformite critique a ete signalee");
        }
        return saved;
    }

    public ResponseEntity<String> supprimerNonConformite(int id) {
        nonConformiteRepo.findById(id).ifPresentOrElse(
            nc -> { nonConformiteRepo.delete(nc); },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Non-conformité non trouvée avec l'id : " + id); }
        );
        return ResponseEntity.ok("Non-conformité supprimée avec succès");
    }

    public ResponseEntity<String> mettreAjourNonConformite(int id, NonConformite nonConformite) {
        nonConformiteRepo.findById(id).ifPresentOrElse(
            nc -> {
                nc.setDescription(nonConformite.getDescription());
                nc.setGravite(nonConformite.getGravite());
                nc.setDate(nonConformite.getDate());
                nc.setRetour(nonConformite.getRetour());
                nc.setCloturee(nonConformite.isCloturee());
                nonConformiteRepo.save(nc);
                if (nc.getGravite() == Gravite.CRITIQUE) {
                    notificationServ.notifierAdmin("Non-conformite critique", "Une non-conformite est maintenant critique");
                }
            },
            () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Non-conformité non trouvée avec l'id : " + id); }
        );
        return ResponseEntity.ok("Non-conformité mise à jour avec succès");
    }
}
