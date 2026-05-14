package com.itbs.retour.dto;

import java.sql.Date;
import com.itbs.retour.entities.EtatTraitement;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class RetourProduitDTO {
    @Schema(description = "ID du retour", example = "1")
    private int id;
    
    @NotBlank(message = "Le produit ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le produit doit avoir entre 3 et 100 caractères")
    @Schema(description = "Nom du produit retourné", example = "Laptop Dell XPS")
    private String produit;
    
    @NotBlank(message = "La raison ne peut pas être vide")
    @Size(min = 5, max = 255, message = "La raison doit avoir entre 5 et 255 caractères")
    @Schema(description = "Raison du retour", example = "Problème d'écran")
    private String raison;
    
    @NotNull(message = "L'état de traitement ne peut pas être null")
    @Schema(description = "État du traitement", example = "EN_ATTENTE")
    private EtatTraitement etatTraitement;
    
    @NotNull(message = "La date ne peut pas être null")
    @Schema(description = "Date du retour", example = "2026-05-06")
    private Date date;
    
    @Schema(description = "Nom du client", example = "Jean Dupont")
    private String nomClient;
}