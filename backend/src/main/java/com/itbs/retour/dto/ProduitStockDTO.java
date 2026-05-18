package com.itbs.retour.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProduitStockDTO {
    @Schema(description = "ID du stock")
    private int id;

    @NotBlank(message = "Le nom du produit ne peut pas etre vide")
    @Size(min = 3, max = 100, message = "Le nom du produit doit avoir entre 3 et 100 caracteres")
    @Schema(description = "Nom du produit")
    private String nomProduit;

    @Min(value = 0, message = "La quantite disponible ne peut pas etre negative")
    @Schema(description = "Quantite disponible en stock")
    private int quantiteDisponible;

    @Min(value = 0, message = "La quantite retournee ne peut pas etre negative")
    @Schema(description = "Quantite cumulee des retours valides")
    private int quantiteRetournee;
}
