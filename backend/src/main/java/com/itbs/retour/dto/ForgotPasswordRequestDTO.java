package com.itbs.retour.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "L'email ne peut pas etre vide")
    @Email(message = "L'email doit etre valide")
    private String email;
}
