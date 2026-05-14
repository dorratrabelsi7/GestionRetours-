package com.itbs.retour.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.itbs.retour.dto.NonConformiteDTO;
import com.itbs.retour.entities.NonConformite;
import com.itbs.retour.entities.Gravite;
import com.itbs.retour.services.NonConformiteService;
import com.itbs.retour.convertors.NonConformiteConvertor;

@RestController
@Tag(name = "NonConformite", description = "API de gestion des non-conformités")
public class NonConformiteController {

    @Autowired
    private NonConformiteService nonConformiteServ;

    @Autowired
    private NonConformiteConvertor nonConformiteConvert;

    @GetMapping("/nonconformites")
    @Operation(summary = "Récupérer toutes les non-conformités")
    @ApiResponse(responseCode = "200", description = "Liste des non-conformités récupérée")
    public List<NonConformiteDTO> trouverToutesLesNonConformites() {
        return nonConformiteConvert.toListDto(nonConformiteServ.trouverToutesLesNonConformites());
    }

    @GetMapping("/nonconformites/{id}")
    @Operation(summary = "Récupérer une non-conformité par ID")
    @ApiResponse(responseCode = "200", description = "Non-conformité trouvée")
    @ApiResponse(responseCode = "404", description = "Non-conformité non trouvée")
    public NonConformiteDTO trouverParId(@PathVariable int id) {
        return nonConformiteConvert.toDto(nonConformiteServ.trouverParId(id));
    }

    @GetMapping("/nonconformites/gravite/{gravite}")
    @Operation(summary = "Récupérer les non-conformités par gravité")
    @ApiResponse(responseCode = "200", description = "Non-conformités trouvées")
    public List<NonConformiteDTO> trouverParGravite(@PathVariable Gravite gravite) {
        return nonConformiteConvert.toListDto(nonConformiteServ.trouverParGravite(gravite));
    }

    @GetMapping("/nonconformites/retour/{retourId}")
    @Operation(summary = "Récupérer les non-conformités d'un retour")
    @ApiResponse(responseCode = "200", description = "Non-conformités trouvées")
    public List<NonConformiteDTO> trouverParRetour(@PathVariable int retourId) {
        return nonConformiteConvert.toListDto(nonConformiteServ.trouverParRetour(retourId));
    }

    @PostMapping("/nonconformites/add")
    @Operation(summary = "Signaler une nouvelle non-conformité")
    @ApiResponse(responseCode = "201", description = "Non-conformité créée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<NonConformiteDTO> signalerNonConformite(@Valid @RequestBody NonConformiteDTO nonConformiteDto) {
        NonConformite nonConformite = new NonConformite();
        nonConformite.setDescription(nonConformiteDto.getDescription());
        nonConformite.setGravite(nonConformiteDto.getGravite());
        nonConformite.setDate(nonConformiteDto.getDate());
        nonConformiteServ.signalerNonConformite(nonConformite);
        return ResponseEntity.status(HttpStatus.CREATED).body(nonConformiteConvert.toDto(nonConformite));
    }

    @PutMapping("/nonconformites/update/{id}")
    @Operation(summary = "Mettre à jour une non-conformité")
    @ApiResponse(responseCode = "200", description = "Non-conformité mise à jour")
    @ApiResponse(responseCode = "404", description = "Non-conformité non trouvée")
    public ResponseEntity<String> mettreAjourNonConformite(@PathVariable int id, @Valid @RequestBody NonConformiteDTO nonConformiteDto) {
        NonConformite nonConformite = new NonConformite();
        nonConformite.setDescription(nonConformiteDto.getDescription());
        nonConformite.setGravite(nonConformiteDto.getGravite());
        nonConformite.setDate(nonConformiteDto.getDate());
        return nonConformiteServ.mettreAjourNonConformite(id, nonConformite);
    }

    @DeleteMapping("/nonconformites/delete/{id}")
    @Operation(summary = "Supprimer une non-conformité")
    @ApiResponse(responseCode = "200", description = "Non-conformité supprimée")
    @ApiResponse(responseCode = "404", description = "Non-conformité non trouvée")
    public ResponseEntity<String> supprimerNonConformite(@PathVariable int id) {
        return nonConformiteServ.supprimerNonConformite(id);
    }
}