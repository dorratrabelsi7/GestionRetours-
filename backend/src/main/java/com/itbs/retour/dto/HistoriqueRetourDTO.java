package com.itbs.retour.dto;

import java.sql.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoriqueRetourDTO {
    // une annotation Swagger/OpenAPI utilisée pour documenter un champ
    @Schema(description = "ID de l'historique")
    private int id;

    @NotBlank(message = "L'action ne peut pas etre vide")
    @Size(min = 5, max = 255, message = "L'action doit avoir entre 5 et 255 caracteres")
    @Schema(description = "Action effectuee")
    private String action;

    @NotNull(message = "La date ne peut pas etre null")
    @Schema(description = "Date de l'action")
    private Date date;

    @Schema(description = "Produit concerne")
    private String produitRetour;

    @Schema(description = "Nom de l'employe")
    private String nomEmploye;

    @Schema(description = "ID du retour associe")
    private Integer retourId;

    @Schema(description = "ID de l'employe ayant realise l'action")
    private Integer employeId;
}
