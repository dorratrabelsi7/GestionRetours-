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
import com.itbs.retour.dto.UtilisateurDTO;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.entities.Role;
import com.itbs.retour.services.UtilisateurService;
import com.itbs.retour.convertors.UtilisateurConvertor;

@RestController
@Tag(name = "Utilisateur", description = "API de gestion des utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurServ;

    @Autowired
    private UtilisateurConvertor utilisateurConvert;

    @GetMapping("/utilisateurs")
    @Operation(summary = "Récupérer tous les utilisateurs")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée")
    public List<UtilisateurDTO> trouverTousLesUtilisateurs() {
        return utilisateurConvert.toListDto(utilisateurServ.trouverTousLesUtilisateurs());
    }

    @GetMapping("/utilisateurs/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public UtilisateurDTO trouverParId(@PathVariable int id) {
        return utilisateurConvert.toDto(utilisateurServ.trouverParId(id));
    }

    @GetMapping("/utilisateurs/role/{role}")
    @Operation(summary = "Récupérer les utilisateurs par rôle")
    @ApiResponse(responseCode = "200", description = "Utilisateurs trouvés")
    public List<UtilisateurDTO> trouverParRole(@PathVariable Role role) {
        return utilisateurConvert.toListDto(utilisateurServ.trouverParRole(role));
    }

    @PostMapping("/utilisateurs/add")
    @Operation(summary = "Ajouter un nouvel utilisateur")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<UtilisateurDTO> ajouterUtilisateur(@Valid @RequestBody UtilisateurDTO utilisateurDto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setRole(utilisateurDto.getRole());
        utilisateurServ.ajouterUtilisateur(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurConvert.toDto(utilisateur));
    }

    @PutMapping("/utilisateurs/update/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public ResponseEntity<String> mettreAjourUtilisateur(@PathVariable int id, @Valid @RequestBody UtilisateurDTO utilisateurDto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setRole(utilisateurDto.getRole());
        return utilisateurServ.mettreAjourUtilisateur(id, utilisateur);
    }

    @DeleteMapping("/utilisateurs/delete/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    @ApiResponse(responseCode = "200", description = "Utilisateur supprimé")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public ResponseEntity<String> supprimerUtilisateur(@PathVariable int id) {
        return utilisateurServ.supprimerUtilisateur(id);
    }
}
