package com.itbs.retour.dto;

import java.sql.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.itbs.retour.entities.Gravite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NonConformiteDTO {
    @Schema(description = "ID de la non-conformite")
    private int id;

    @NotBlank(message = "La description ne peut pas etre vide")
    @Size(min = 5, max = 255, message = "La description doit avoir entre 5 et 255 caracteres")
    @Schema(description = "Description de la non-conformite")
    private String description;

    @NotNull(message = "La gravite ne peut pas etre null")
    @Schema(description = "Niveau de gravite")
    private Gravite gravite;

    @Schema(description = "Date de la non-conformite")
    private Date date;

    @Schema(description = "Non-conformite cloturee")
    private boolean cloturee;

    @Schema(description = "Produit concerne")
    private String produitRetour;

    @Schema(description = "ID du retour associe")
    private Integer retourId;
}
