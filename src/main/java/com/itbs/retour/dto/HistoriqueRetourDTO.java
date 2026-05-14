package com.itbs.retour.dto;

import java.sql.Date;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class HistoriqueRetourDTO {
    @Schema(description = "ID de l'historique", example = "1")
    private int id;
    
    @NotBlank(message = "L'action ne peut pas être vide")
    @Size(min = 5, max = 255, message = "L'action doit avoir entre 5 et 255 caractères")
    @Schema(description = "Action effectuée", example = "Retour reçu et inspecté")
    private String action;
    
    @NotNull(message = "La date ne peut pas être null")
    @Schema(description = "Date de l'action", example = "2026-05-06")
    private Date date;
    
    @Schema(description = "Produit concerné", example = "Laptop Dell XPS")
    private String produitRetour;
    
    @Schema(description = "Nom de l'employé", example = "Marie Martin")
    private String nomEmploye;
}