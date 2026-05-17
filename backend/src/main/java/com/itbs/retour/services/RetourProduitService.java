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
import com.itbs.retour.entities.Role;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.repositories.HistoriqueRetourRepository;
import com.itbs.retour.repositories.NonConformiteRepository;
import com.itbs.retour.repositories.RetourProduitRepository;
import com.itbs.retour.repositories.UtilisateurRepository;

@Service
public class RetourProduitService {

    @Autowired
    private RetourProduitRepository retourRepo;

    @Autowired
    private ProduitStockService stockServ;

    @Autowired
    private HistoriqueRetourRepository historiqueRepo;

    @Autowired
    private NonConformiteRepository nonConformiteRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private NotificationService notificationServ;

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
        retour.setDate(new Date(System.currentTimeMillis()));
        retour.setEtatTraitement(EtatTraitement.EN_ATTENTE);
        retour.setStockMisAJour(false);
        RetourProduit saved = retourRepo.save(retour);
        ajouterHistoriqueSysteme(saved, "Retour enregistre");
        notificationServ.notifierRole(Role.EMPLOYE, "Nouveau retour", "Un client a soumis un retour pour le produit " + saved.getProduit());
        notificationServ.notifierRole(Role.QUALITE, "Nouveau retour", "Un nouveau retour est en attente pour le produit " + saved.getProduit());
        notificationServ.notifierAdmin("Nouveau retour", "Un nouveau retour a ete soumis pour le produit " + saved.getProduit());
        return saved;
    }

    @Transactional
    public ResponseEntity<String> supprimerRetour(int id) {
        RetourProduit retour = trouverRetourParId(id);
        historiqueRepo.deleteByRetourId(id);
        nonConformiteRepo.deleteByRetourId(id);
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
    public ResponseEntity<String> prendreEnChargeRetour(int id, Integer employeId) {
        RetourProduit retour = trouverRetourParId(id);
        retour.setEtatTraitement(EtatTraitement.EN_COURS);
        retourRepo.save(retour);
        Utilisateur employe = trouverUtilisateur(employeId);
        ajouterHistorique(retour, "Retour pris en charge", employe);
        notificationServ.notifierUtilisateur(retour.getClient(), "Retour en cours", "Votre retour du produit " + retour.getProduit() + " est en cours de traitement");
        return ResponseEntity.ok("Retour pris en charge avec succes");
    }

    @Transactional
    public ResponseEntity<String> validerRetour(int id, Integer employeId) {
        RetourProduit retour = trouverRetourParId(id);
        retour.setEtatTraitement(EtatTraitement.ACCEPTE);
        if (!retour.isStockMisAJour()) {
            stockServ.incrementerRetourProduit(retour.getProduit(), retour.getQuantite());
            retour.setStockMisAJour(true);
        }
        retourRepo.save(retour);
        ajouterHistorique(retour, "Retour accepte par le service qualite", trouverUtilisateur(employeId));
        notificationServ.notifierUtilisateur(retour.getClient(), "Retour accepte", "Votre retour du produit " + retour.getProduit() + " a ete accepte");
        notificationServ.notifierAdmin("Stock mis a jour", "Le stock du produit " + retour.getProduit() + " a ete mis a jour apres acceptation du retour");
        return ResponseEntity.ok("Retour accepte avec succes");
    }

    @Transactional
    public ResponseEntity<String> rejeterRetour(int id, String commentaire, Integer employeId) {
        if (commentaire == null || commentaire.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le commentaire de refus est obligatoire");
        }
        RetourProduit retour = trouverRetourParId(id);
        retour.setEtatTraitement(EtatTraitement.REFUSE);
        retourRepo.save(retour);
        ajouterHistorique(retour, "Retour refuse par le service qualite : " + commentaire, trouverUtilisateur(employeId));
        notificationServ.notifierUtilisateur(retour.getClient(), "Retour refuse", "Votre retour du produit " + retour.getProduit() + " a ete refuse : " + commentaire);
        notificationServ.notifierAdmin("Retour refuse", "Le retour du produit " + retour.getProduit() + " a ete refuse");
        return ResponseEntity.ok("Retour refuse");
    }

    private void ajouterHistoriqueSysteme(RetourProduit retour, String action) {
        ajouterHistorique(retour, action, null);
    }

    private void ajouterHistorique(RetourProduit retour, String action, Utilisateur employe) {
        HistoriqueRetour historique = new HistoriqueRetour();
        historique.setRetour(retour);
        historique.setAction(action);
        historique.setEmploye(employe);
        historique.setDate(new Date(System.currentTimeMillis()));
        historiqueRepo.save(historique);
    }

    private Utilisateur trouverUtilisateur(Integer utilisateurId) {
        if (utilisateurId == null) return null;
        return utilisateurRepo.findById(utilisateurId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouve avec l'id : " + utilisateurId));
    }
}
