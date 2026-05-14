package com.itbs.retour.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.itbs.retour.convertors.ProduitStockConvertor;
import com.itbs.retour.dto.ProduitStockDTO;
import com.itbs.retour.entities.ProduitStock;
import com.itbs.retour.services.ProduitStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "ProduitStock", description = "API de gestion du stock produits")
public class ProduitStockController {

    @Autowired
    private ProduitStockService stockServ;

    @Autowired
    private ProduitStockConvertor stockConvert;

    @GetMapping("/stocks")
    @Operation(summary = "Recuperer tous les stocks")
    @ApiResponse(responseCode = "200", description = "Liste des stocks recuperee")
    public List<ProduitStockDTO> trouverTousLesStocks() {
        return stockConvert.toListDto(stockServ.trouverTousLesStocks());
    }

    @GetMapping("/stocks/{id}")
    @Operation(summary = "Recuperer un stock par ID")
    public ProduitStockDTO trouverParId(@PathVariable int id) {
        return stockConvert.toDto(stockServ.trouverParId(id));
    }

    @GetMapping("/stocks/produit/{nomProduit}")
    @Operation(summary = "Recuperer un stock par nom de produit")
    public ProduitStockDTO trouverParProduit(@PathVariable String nomProduit) {
        return stockConvert.toDto(stockServ.trouverParProduit(nomProduit));
    }

    @PostMapping("/stocks/add")
    @Operation(summary = "Ajouter un produit au stock")
    public ResponseEntity<ProduitStockDTO> enregistrerStock(@Valid @RequestBody ProduitStockDTO stockDto) {
        ProduitStock stock = stockConvert.fromDto(stockDto);
        ProduitStock saved = stockServ.enregistrerStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(stockConvert.toDto(saved));
    }

    @PutMapping("/stocks/update/{id}")
    @Operation(summary = "Mettre a jour un stock")
    public ResponseEntity<String> mettreAjourStock(@PathVariable int id, @Valid @RequestBody ProduitStockDTO stockDto) {
        return stockServ.mettreAjourStock(id, stockConvert.fromDto(stockDto));
    }

    @DeleteMapping("/stocks/delete/{id}")
    @Operation(summary = "Supprimer un stock")
    public ResponseEntity<String> supprimerStock(@PathVariable int id) {
        return stockServ.supprimerStock(id);
    }
}
