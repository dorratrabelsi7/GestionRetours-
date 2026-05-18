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
    @Schema(description = "ID du retour")
    private int id;

    @NotBlank(message = "Le produit ne peut pas etre vide")
    @Size(min = 3, max = 100, message = "Le produit doit avoir entre 3 et 100 caracteres")
    @Schema(description = "Nom du produit retourne")
    private String produit;

    @NotBlank(message = "La raison ne peut pas etre vide")
    @Size(min = 5, max = 255, message = "La raison doit avoir entre 5 et 255 caracteres")
    @Schema(description = "Raison du retour")
    private String raison;

    @Min(value = 1, message = "La quantite doit etre au moins egale a 1")
    @Schema(description = "Quantite retournee")
    private int quantite = 1;

    @Schema(description = "Etat du traitement")
    private EtatTraitement etatTraitement;

    @Schema(description = "Date du retour")
    private Date date;

    @Schema(description = "Nom du client")
    private String nomClient;

    @Schema(description = "ID du client")
    private Integer clientId;

    @Schema(description = "Indique si le stock a deja ete mis a jour")
    private boolean stockMisAJour;
}
