package com.itbs.retour.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.convertors.UtilisateurConvertor;
import com.itbs.retour.dto.ForgotPasswordRequestDTO;
import com.itbs.retour.dto.LoginRequestDTO;
import com.itbs.retour.dto.ResetPasswordRequestDTO;
import com.itbs.retour.dto.UtilisateurDTO;
import com.itbs.retour.entities.Role;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.repositories.UtilisateurRepository;
import com.itbs.retour.services.PasswordResetService;
import com.itbs.retour.services.UtilisateurService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Authentification", description = "Login et inscription des acteurs")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private UtilisateurService utilisateurServ;

    @Autowired
    private UtilisateurConvertor utilisateurConvert;

    @Autowired
    private PasswordResetService passwordResetServ;

    @PostMapping("/auth/login")
    public UtilisateurDTO login(@Valid @RequestBody LoginRequestDTO request) {
        Utilisateur utilisateur = utilisateurRepo.findByEmailIgnoreCase(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));
        if (utilisateur.getMotDePasse() == null || !utilisateur.getMotDePasse().equals(request.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }
        return utilisateurConvert.toDto(utilisateur);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UtilisateurDTO> signup(@Valid @RequestBody UtilisateurDTO utilisateurDto) {
        utilisateurRepo.findByEmailIgnoreCase(utilisateurDto.getEmail()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email existe deja");
        });
        Utilisateur utilisateur = utilisateurConvert.fromDto(utilisateurDto);
        utilisateur.setRole(Role.CLIENT);
        utilisateurServ.ajouterUtilisateur(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurConvert.toDto(utilisateur));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetServ.demanderReinitialisation(request.getEmail());
        return ResponseEntity.ok("Email de reinitialisation envoye");
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordResetServ.reinitialiserMotDePasse(request.getToken(), request.getMotDePasse());
        return ResponseEntity.ok("Mot de passe mis a jour");
    }
}
