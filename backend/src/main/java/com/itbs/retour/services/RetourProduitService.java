package com.itbs.retour.services;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.EtatTraitement;
import com.itbs.retour.entities.HistoriqueRetour;
import com.itbs.retour.entities.RetourProduit;
import com.itbs.retour.repositories.HistoriqueRetourRepository;
import com.itbs.retour.repositories.RetourProduitRepository;

@Service
public class RetourProduitService {

    @Autowired
    private RetourProduitRepository retourRepo;

    @Autowired
    private ProduitStockService stockServ;

    @Autowired
    private HistoriqueRetourRepository historiqueRepo;

    public List<RetourProduit> trouverTousLesRetours() {
        return retourRepo.findAll();
    }

    public RetourProduit trouverRetourParId(int id) {
        return retourRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Retour non trouve avec l'id : " + id));
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

    @Transactional
    public RetourProduit enregistrerRetour(RetourProduit retour) {
        RetourProduit saved = retourRepo.save(retour);
        ajouterHistoriqueSysteme(saved, "Retour enregistre");
        return saved;
    }

    public ResponseEntity<String> supprimerRetour(int id) {
        RetourProduit retour = trouverRetourParId(id);
        retourRepo.delete(retour);
        return ResponseEntity.ok("Retour supprime avec succes");
    }

    public ResponseEntity<String> mettreAjourRetour(int id, RetourProduit retour) {
        RetourProduit existing = trouverRetourParId(id);
        existing.setProduit(retour.getProduit());
        existing.setRaison(retour.getRaison());
        existing.setQuantite(retour.getQuantite());
        existing.setEtatTraitement(retour.getEtatTraitement());
        existing.setDate(retour.getDate());
        retourRepo.save(existing);
        return ResponseEntity.ok("Retour mis a jour avec succes");
    }

    @Transactional
    public ResponseEntity<String> validerRetour(int id) {
        RetourProduit retour = trouverRetourParId(id);
        retour.setEtatTraitement(EtatTraitement.VALIDE);
        if (!retour.isStockMisAJour()) {
            stockServ.incrementerRetourProduit(retour.getProduit(), retour.getQuantite());
            retour.setStockMisAJour(true);
        }
        retourRepo.save(retour);
        ajouterHistoriqueSysteme(retour, "Retour valide par le service qualite");
        return ResponseEntity.ok("Retour valide avec succes");
    }

    @Transactional
    public ResponseEntity<String> rejeterRetour(int id) {
        RetourProduit retour = trouverRetourParId(id);
        retour.setEtatTraitement(EtatTraitement.REJETE);
        retourRepo.save(retour);
        ajouterHistoriqueSysteme(retour, "Retour rejete par le service qualite");
        return ResponseEntity.ok("Retour rejete");
    }

    private void ajouterHistoriqueSysteme(RetourProduit retour, String action) {
        HistoriqueRetour historique = new HistoriqueRetour();
        historique.setRetour(retour);
        historique.setAction(action);
        historique.setDate(new Date(System.currentTimeMillis()));
        historiqueRepo.save(historique);
    }
}
