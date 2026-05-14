package com.itbs.retour.controllers;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.itbs.retour.dto.RetourProduitDTO;
import com.itbs.retour.entities.RetourProduit;
import com.itbs.retour.entities.EtatTraitement;
import com.itbs.retour.services.RetourProduitService;
import com.itbs.retour.convertors.RetourProduitConvertor;

@RestController
@Tag(name = "RetourProduit", description = "API de gestion des retours produits")
public class RetourProduitController {

    @Autowired
    private RetourProduitService retourServ;

    @Autowired
    private RetourProduitConvertor retourConvert;

    @GetMapping("/retours")
    @Operation(summary = "Récupérer tous les retours")
    @ApiResponse(responseCode = "200", description = "Liste des retours récupérée avec succès")
    public List<RetourProduitDTO> trouverTousLesRetours() {
        return retourConvert.toListDto(retourServ.trouverTousLesRetours());
    }

    @GetMapping("/retours/{id}")
    @Operation(summary = "Récupérer un retour par ID")
    @ApiResponse(responseCode = "200", description = "Retour trouvé")
    @ApiResponse(responseCode = "404", description = "Retour non trouvé")
    public RetourProduitDTO trouverRetourParId(@PathVariable int id) {
        return retourConvert.toDto(retourServ.trouverRetourParId(id));
    }

    @GetMapping("/retours/etat/{etat}")
    @Operation(summary = "Récupérer les retours par état de traitement")
    @ApiResponse(responseCode = "200", description = "Retours trouvés")
    public List<RetourProduitDTO> trouverParEtat(@PathVariable EtatTraitement etat) {
        return retourConvert.toListDto(retourServ.trouverParEtat(etat));
    }

    @GetMapping("/retours/client/{clientId}")
    @Operation(summary = "Récupérer les retours d'un client")
    @ApiResponse(responseCode = "200", description = "Retours du client récupérés")
    public List<RetourProduitDTO> trouverParClient(@PathVariable int clientId) {
        return retourConvert.toListDto(retourServ.trouverParClient(clientId));
    }

    @GetMapping("/retours/periode")
    @Operation(summary = "Récupérer les retours par période")
    @ApiResponse(responseCode = "200", description = "Retours de la période récupérés")
    public List<RetourProduitDTO> trouverParPeriode(@RequestParam Date dateDebut, @RequestParam Date dateFin) {
        return retourConvert.toListDto(retourServ.trouverParPeriode(dateDebut, dateFin));
    }

    @PostMapping("/retours/add")
    @Operation(summary = "Enregistrer un nouveau retour")
    @ApiResponse(responseCode = "201", description = "Retour créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<RetourProduitDTO> enregistrerRetour(@Valid @RequestBody RetourProduitDTO retourDto) {
        RetourProduit retour = new RetourProduit();
        retour.setProduit(retourDto.getProduit());
        retour.setRaison(retourDto.getRaison());
        retour.setEtatTraitement(retourDto.getEtatTraitement());
        retour.setDate(retourDto.getDate());
        retourServ.enregistrerRetour(retour);
        return ResponseEntity.status(HttpStatus.CREATED).body(retourConvert.toDto(retour));
    }

    @PutMapping("/retours/update/{id}")
    @Operation(summary = "Mettre à jour un retour")
    @ApiResponse(responseCode = "200", description = "Retour mis à jour")
    @ApiResponse(responseCode = "404", description = "Retour non trouvé")
    public ResponseEntity<String> mettreAjourRetour(@PathVariable int id, @Valid @RequestBody RetourProduitDTO retourDto) {
        RetourProduit retour = new RetourProduit();
        retour.setProduit(retourDto.getProduit());
        retour.setRaison(retourDto.getRaison());
        retour.setEtatTraitement(retourDto.getEtatTraitement());
        retour.setDate(retourDto.getDate());
        return retourServ.mettreAjourRetour(id, retour);
    }

    @PutMapping("/retours/valider/{id}")
    @Operation(summary = "Valider un retour")
    @ApiResponse(responseCode = "200", description = "Retour validé")
    @ApiResponse(responseCode = "404", description = "Retour non trouvé")
    public ResponseEntity<String> validerRetour(@PathVariable int id) {
        return retourServ.validerRetour(id);
    }

    @PutMapping("/retours/rejeter/{id}")
    @Operation(summary = "Rejeter un retour")
    @ApiResponse(responseCode = "200", description = "Retour rejeté")
    @ApiResponse(responseCode = "404", description = "Retour non trouvé")
    public ResponseEntity<String> rejeterRetour(@PathVariable int id) {
        return retourServ.rejeterRetour(id);
    }

    @DeleteMapping("/retours/delete/{id}")
    @Operation(summary = "Supprimer un retour")
    @ApiResponse(responseCode = "200", description = "Retour supprimé")
    @ApiResponse(responseCode = "404", description = "Retour non trouvé")
    public ResponseEntity<String> supprimerRetour(@PathVariable int id) {
        return retourServ.supprimerRetour(id);
    }
}