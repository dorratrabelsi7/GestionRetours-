package com.itbs.retour.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProduitStockDTO {
    @Schema(description = "ID du stock", example = "1")
    private int id;

    @NotBlank(message = "Le nom du produit ne peut pas etre vide")
    @Size(min = 3, max = 100, message = "Le nom du produit doit avoir entre 3 et 100 caracteres")
    @Schema(description = "Nom du produit", example = "Laptop Dell XPS")
    private String nomProduit;

    @Min(value = 0, message = "La quantite disponible ne peut pas etre negative")
    @Schema(description = "Quantite disponible en stock", example = "15")
    private int quantiteDisponible;

    @Min(value = 0, message = "La quantite retournee ne peut pas etre negative")
    @Schema(description = "Quantite cumulee des retours valides", example = "3")
    private int quantiteRetournee;
}
