package com.itbs.retour.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.ProduitStock;
import com.itbs.retour.repositories.ProduitStockRepository;

@Service
public class ProduitStockService {

    @Autowired
    private ProduitStockRepository stockRepo;

    public List<ProduitStock> trouverTousLesStocks() {
        return stockRepo.findAll();
    }

    public ProduitStock trouverParId(int id) {
        return stockRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock non trouve avec l'id : " + id));
    }

    public ProduitStock trouverParProduit(String nomProduit) {
        return stockRepo.findByNomProduitIgnoreCase(nomProduit)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock non trouve pour le produit : " + nomProduit));
    }

    public ProduitStock enregistrerStock(ProduitStock stock) {
        stockRepo.findByNomProduitIgnoreCase(stock.getNomProduit()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce produit existe deja dans le stock");
        });
        return stockRepo.save(stock);
    }

    public ResponseEntity<String> mettreAjourStock(int id, ProduitStock stock) {
        ProduitStock existing = trouverParId(id);
        existing.setNomProduit(stock.getNomProduit());
        existing.setQuantiteDisponible(stock.getQuantiteDisponible());
        existing.setQuantiteRetournee(stock.getQuantiteRetournee());
        stockRepo.save(existing);
        return ResponseEntity.ok("Stock mis a jour avec succes");
    }

    public ResponseEntity<String> supprimerStock(int id) {
        ProduitStock stock = trouverParId(id);
        stockRepo.delete(stock);
        return ResponseEntity.ok("Stock supprime avec succes");
    }

    public ProduitStock incrementerRetourProduit(String produit, int quantite) {
        ProduitStock stock = stockRepo.findByNomProduitIgnoreCase(produit).orElseGet(() -> {
            ProduitStock created = new ProduitStock();
            created.setNomProduit(produit);
            created.setQuantiteDisponible(0);
            created.setQuantiteRetournee(0);
            return created;
        });
        stock.setQuantiteDisponible(stock.getQuantiteDisponible() + quantite);
        stock.setQuantiteRetournee(stock.getQuantiteRetournee() + quantite);
        return stockRepo.save(stock);
    }
}
