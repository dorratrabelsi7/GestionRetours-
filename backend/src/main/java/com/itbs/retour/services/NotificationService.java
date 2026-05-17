package com.itbs.retour.services;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itbs.retour.entities.Notification;
import com.itbs.retour.entities.Role;
import com.itbs.retour.entities.Utilisateur;
import com.itbs.retour.repositories.NotificationRepository;
import com.itbs.retour.repositories.UtilisateurRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    public List<Notification> trouverToutesLesNotifications() {
        return notificationRepo.findAll();
    }

    public List<Notification> trouverParDestinataire(int destinataireId) {
        return notificationRepo.findByDestinataireId(destinataireId);
    }

    public List<Notification> trouverParRole(Role role) {
        return notificationRepo.findByRoleDestinataire(role);
    }

    public long compterNonLuesUtilisateur(int destinataireId) {
        return notificationRepo.countByDestinataireIdAndLueFalse(destinataireId);
    }

    public void notifierUtilisateur(Utilisateur utilisateur, String titre, String message) {
        if (utilisateur == null) return;
        Notification notification = creerNotification(titre, message);
        notification.setDestinataire(utilisateur);
        notification.setRoleDestinataire(utilisateur.getRole());
        notificationRepo.save(notification);
    }

    public void notifierRole(Role role, String titre, String message) {
        for (Utilisateur utilisateur : utilisateurRepo.findByRole(role)) {
            notifierUtilisateur(utilisateur, titre, message);
        }
    }

    public void notifierAdmin(String titre, String message) {
        notifierRole(Role.ADMIN, titre, message);
    }

    public ResponseEntity<String> marquerCommeLue(int id) {
        Notification notification = notificationRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification non trouvee avec l'id : " + id));
        notification.setLue(true);
        notificationRepo.save(notification);
        return ResponseEntity.ok("Notification marquee comme lue");
    }

    public ResponseEntity<String> marquerToutesCommeLues(int destinataireId) {
        List<Notification> notifications = notificationRepo.findByDestinataireId(destinataireId);
        for (Notification notification : notifications) {
            notification.setLue(true);
        }
        notificationRepo.saveAll(notifications);
        return ResponseEntity.ok("Notifications marquees comme lues");
    }

    public ResponseEntity<String> supprimerNotification(int id) {
        Notification notification = notificationRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification non trouvee avec l'id : " + id));
        notificationRepo.delete(notification);
        return ResponseEntity.ok("Notification supprimee avec succes");
    }

    private Notification creerNotification(String titre, String message) {
        Notification notification = new Notification();
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setDate(new Date(System.currentTimeMillis()));
        notification.setLue(false);
        return notification;
    }
}
