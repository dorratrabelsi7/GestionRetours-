package com.itbs.retour.dto;

import java.sql.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoriqueRetourDTO {
    @Schema(description = "ID de l'historique", example = "1")
    private int id;

    @NotBlank(message = "L'action ne peut pas etre vide")
    @Size(min = 5, max = 255, message = "L'action doit avoir entre 5 et 255 caracteres")
    @Schema(description = "Action effectuee", example = "Retour recu et inspecte")
    private String action;

    @NotNull(message = "La date ne peut pas etre null")
    @Schema(description = "Date de l'action", example = "2026-05-06")
    private Date date;

    @Schema(description = "Produit concerne", example = "Laptop Dell XPS")
    private String produitRetour;

    @Schema(description = "Nom de l'employe", example = "Marie Martin")
    private String nomEmploye;

    @Schema(description = "ID du retour associe", example = "1")
    private Integer retourId;

    @Schema(description = "ID de l'employe ayant realise l'action", example = "2")
    private Integer employeId;
}
