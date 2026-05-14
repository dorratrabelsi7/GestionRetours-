package com.itbs.retour.dto;

import com.itbs.retour.entities.Role;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class UtilisateurDTO {
    @Schema(description = "ID de l'utilisateur", example = "1")
    private int id;
    
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom doit avoir entre 3 et 50 caractères")
    @Schema(description = "Nom de l'utilisateur", example = "Jean Dupont")
    private String nom;
    
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    @Schema(description = "Email de l'utilisateur", example = "jean@example.com")
    private String email;
    
    @NotNull(message = "Le rôle ne peut pas être null")
    @Schema(description = "Rôle de l'utilisateur", example = "QUALITE")
    private Role role;
}