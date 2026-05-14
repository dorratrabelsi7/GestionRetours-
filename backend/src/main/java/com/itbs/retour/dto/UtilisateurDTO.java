package com.itbs.retour.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.itbs.retour.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UtilisateurDTO {
    @Schema(description = "ID de l'utilisateur", example = "1")
    private int id;

    @NotBlank(message = "Le nom ne peut pas etre vide")
    @Size(min = 3, max = 50, message = "Le nom doit avoir entre 3 et 50 caracteres")
    @Schema(description = "Nom de l'utilisateur", example = "Jean Dupont")
    private String nom;

    @NotBlank(message = "L'email ne peut pas etre vide")
    @Email(message = "L'email doit etre valide")
    @Schema(description = "Email de l'utilisateur", example = "jean@example.com")
    private String email;

    @Size(min = 4, max = 80, message = "Le mot de passe doit avoir entre 4 et 80 caracteres")
    @Schema(description = "Mot de passe", example = "admin123")
    private String motDePasse;

    @NotNull(message = "Le role ne peut pas etre null")
    @Schema(description = "Role de l'utilisateur", example = "QUALITE")
    private Role role;
}
