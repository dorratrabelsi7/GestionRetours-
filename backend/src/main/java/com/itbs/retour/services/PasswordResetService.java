package com.itbs.retour.services;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.PasswordResetToken;
import com.itbs.retour.entities.Role;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.repositories.PasswordResetTokenRepository;
import com.itbs.retour.repositories.UtilisateurRepository;

@Service
public class PasswordResetService {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String mailFrom;

    @Transactional
    public void demanderReinitialisation(String email) {
        Utilisateur utilisateur = utilisateurRepo.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun client trouve avec cet email"));
        if (utilisateur.getRole() != Role.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La reinitialisation est accessible uniquement aux clients");
        }
        if (mailUsername == null || mailUsername.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "SMTP non configure sur le serveur");
        }

        tokenRepo.deleteByUtilisateurAndUsedFalse(utilisateur);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUtilisateur(utilisateur);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        tokenRepo.save(resetToken);

        envoyerEmail(utilisateur, resetToken.getToken());
    }

    @Transactional
    public void reinitialiserMotDePasse(String token, String motDePasse) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lien de reinitialisation invalide"));
        if (resetToken.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce lien a deja ete utilise");
        }
        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce lien a expire");
        }
        Utilisateur utilisateur = resetToken.getUtilisateur();
        if (utilisateur.getRole() != Role.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La reinitialisation est accessible uniquement aux clients");
        }
        utilisateur.setMotDePasse(motDePasse);
        utilisateurRepo.save(utilisateur);
        resetToken.setUsed(true);
        tokenRepo.save(resetToken);
    }

    private void envoyerEmail(Utilisateur utilisateur, String token) {
        String link = frontendUrl.replaceAll("/$", "") + "?resetToken=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(utilisateur.getEmail());
        message.setSubject("Reinitialisation de votre mot de passe Gestion Retours");
        message.setText(
            "Bonjour " + utilisateur.getNom() + ",\n\n"
            + "Cliquez sur ce lien pour changer votre mot de passe :\n"
            + link + "\n\n"
            + "Ce lien expire dans 30 minutes.\n"
            + "Si vous n'avez pas demande cette action, ignorez cet email."
        );
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email non envoye. Verifiez la configuration SMTP.", ex);
        }
    }
}
