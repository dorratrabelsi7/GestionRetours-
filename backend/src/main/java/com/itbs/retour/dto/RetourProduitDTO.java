package com.itbs.retour.dto;

import java.sql.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.itbs.retour.entities.EtatTraitement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RetourProduitDTO {
    @Schema(description = "ID du retour", example = "1")
    private int id;

    @NotBlank(message = "Le produit ne peut pas etre vide")
    @Size(min = 3, max = 100, message = "Le produit doit avoir entre 3 et 100 caracteres")
    @Schema(description = "Nom du produit retourne", example = "Laptop Dell XPS")
    private String produit;

    @NotBlank(message = "La raison ne peut pas etre vide")
    @Size(min = 5, max = 255, message = "La raison doit avoir entre 5 et 255 caracteres")
    @Schema(description = "Raison du retour", example = "Probleme d'ecran")
    private String raison;

    @Min(value = 1, message = "La quantite doit etre au moins egale a 1")
    @Schema(description = "Quantite retournee", example = "2")
    private int quantite = 1;

    @Schema(description = "Etat du traitement", example = "EN_ATTENTE")
    private EtatTraitement etatTraitement;

    @Schema(description = "Date du retour", example = "2026-05-06")
    private Date date;

    @Schema(description = "Nom du client", example = "Jean Dupont")
    private String nomClient;

    @Schema(description = "ID du client", example = "1")
    private Integer clientId;

    @Schema(description = "Indique si le stock a deja ete mis a jour", example = "false")
    private boolean stockMisAJour;
}
