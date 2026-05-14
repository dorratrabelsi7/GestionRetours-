package com.itbs.retour.dto;

import java.sql.Date;
import com.itbs.retour.entities.Gravite;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class NonConformiteDTO {
    @Schema(description = "ID de la non-conformité", example = "1")
    private int id;
    
    @NotBlank(message = "La description ne peut pas être vide")
    @Size(min = 5, max = 255, message = "La description doit avoir entre 5 et 255 caractères")
    @Schema(description = "Description de la non-conformité", example = "Défaut de fabrication détecté")
    private String description;
    
    @NotNull(message = "La gravité ne peut pas être null")
    @Schema(description = "Niveau de gravité", example = "MOYENNE")
    private Gravite gravite;
    
    @NotNull(message = "La date ne peut pas être null")
    @Schema(description = "Date de la non-conformité", example = "2026-05-06")
    private Date date;
    
    @Schema(description = "Produit concerné", example = "Laptop Dell XPS")
    private String produitRetour;
}