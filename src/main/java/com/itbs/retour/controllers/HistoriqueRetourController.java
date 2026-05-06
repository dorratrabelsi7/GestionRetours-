package com.itbs.retour.controllers;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.itbs.retour.dto.HistoriqueRetourDTO;
import com.itbs.retour.entities.HistoriqueRetour;
import com.itbs.retour.services.HistoriqueRetourService;
import com.itbs.retour.convertors.HistoriqueRetourConvertor;

@RestController
@Tag(name = "HistoriqueRetour", description = "API de gestion de l'historique des retours")
public class HistoriqueRetourController {

    @Autowired
    private HistoriqueRetourService historiqueServ;

    @Autowired
    private HistoriqueRetourConvertor historiqueConvert;

    @GetMapping("/historiques")
    @Operation(summary = "Récupérer tous les historiques")
    @ApiResponse(responseCode = "200", description = "Liste des historiques récupérée")
    public List<HistoriqueRetourDTO> trouverTousLesHistoriques() {
        return historiqueConvert.toListDto(historiqueServ.trouverTousLesHistoriques());
    }

    @GetMapping("/historiques/{id}")
    @Operation(summary = "Récupérer un historique par ID")
    @ApiResponse(responseCode = "200", description = "Historique trouvé")
    @ApiResponse(responseCode = "404", description = "Historique non trouvé")
    public HistoriqueRetourDTO trouverParId(@PathVariable int id) {
        return historiqueConvert.toDto(historiqueServ.trouverParId(id));
    }

    @GetMapping("/historiques/retour/{retourId}")
    @Operation(summary = "Récupérer l'historique d'un retour")
    @ApiResponse(responseCode = "200", description = "Historiques trouvés")
    public List<HistoriqueRetourDTO> trouverParRetour(@PathVariable int retourId) {
        return historiqueConvert.toListDto(historiqueServ.trouverParRetour(retourId));
    }

    @GetMapping("/historiques/employe/{employeId}")
    @Operation(summary = "Récupérer l'historique d'un employé")
    @ApiResponse(responseCode = "200", description = "Historiques trouvés")
    public List<HistoriqueRetourDTO> trouverParEmploye(@PathVariable int employeId) {
        return historiqueConvert.toListDto(historiqueServ.trouverParEmploye(employeId));
    }

    @GetMapping("/historiques/periode")
    @Operation(summary = "Récupérer l'historique par période")
    @ApiResponse(responseCode = "200", description = "Historiques trouvés")
    public List<HistoriqueRetourDTO> trouverParPeriode(@RequestParam Date dateDebut, @RequestParam Date dateFin) {
        return historiqueConvert.toListDto(historiqueServ.trouverParPeriode(dateDebut, dateFin));
    }

    @PostMapping("/historiques/add")
    @Operation(summary = "Enregistrer une nouvelle action dans l'historique")
    @ApiResponse(responseCode = "201", description = "Historique créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<HistoriqueRetourDTO> enregistrerAction(@Valid @RequestBody HistoriqueRetourDTO historiqueDto) {
        HistoriqueRetour historique = new HistoriqueRetour();
        historique.setAction(historiqueDto.getAction());
        historique.setDate(historiqueDto.getDate());
        historiqueServ.enregistrerAction(historique);
        return ResponseEntity.status(HttpStatus.CREATED).body(historiqueConvert.toDto(historique));
    }

    @DeleteMapping("/historiques/delete/{id}")
    @Operation(summary = "Supprimer un historique")
    @ApiResponse(responseCode = "200", description = "Historique supprimé")
    @ApiResponse(responseCode = "404", description = "Historique non trouvé")
    public ResponseEntity<String> supprimerHistorique(@PathVariable int id) {
        return historiqueServ.supprimerHistorique(id);
    }
}