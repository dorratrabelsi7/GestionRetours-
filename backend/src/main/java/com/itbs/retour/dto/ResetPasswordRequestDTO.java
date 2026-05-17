package com.itbs.retour.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Le token est obligatoire")
    private String token;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caracteres")
    private String motDePasse;
}
