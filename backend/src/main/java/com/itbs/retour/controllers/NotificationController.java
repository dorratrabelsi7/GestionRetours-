package com.itbs.retour.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itbs.retour.convertors.NotificationConvertor;
import com.itbs.retour.dto.NotificationDTO;
import com.itbs.retour.entities.Role;
import com.itbs.retour.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Notification", description = "API de gestion des notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationServ;

    @Autowired
    private NotificationConvertor notificationConvert;

    @GetMapping("/notifications")
    @Operation(summary = "Recuperer toutes les notifications")
    public List<NotificationDTO> trouverToutesLesNotifications() {
        return notificationConvert.toListDto(notificationServ.trouverToutesLesNotifications());
    }

    @GetMapping("/notifications/utilisateur/{destinataireId}")
    @Operation(summary = "Recuperer les notifications d'un utilisateur")
    public List<NotificationDTO> trouverParDestinataire(@PathVariable int destinataireId) {
        return notificationConvert.toListDto(notificationServ.trouverParDestinataire(destinataireId));
    }

    @GetMapping("/notifications/role/{role}")
    @Operation(summary = "Recuperer les notifications par role")
    public List<NotificationDTO> trouverParRole(@PathVariable Role role) {
        return notificationConvert.toListDto(notificationServ.trouverParRole(role));
    }

    @GetMapping("/notifications/nonlues/{destinataireId}")
    @Operation(summary = "Compter les notifications non lues")
    public long compterNonLuesUtilisateur(@PathVariable int destinataireId) {
        return notificationServ.compterNonLuesUtilisateur(destinataireId);
    }

    @PutMapping("/notifications/lire/{id}")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<String> marquerCommeLue(@PathVariable int id) {
        return notificationServ.marquerCommeLue(id);
    }

    @PutMapping("/notifications/lire-toutes/{destinataireId}")
    @Operation(summary = "Marquer toutes les notifications d'un utilisateur comme lues")
    public ResponseEntity<String> marquerToutesCommeLues(@PathVariable int destinataireId) {
        return notificationServ.marquerToutesCommeLues(destinataireId);
    }

    @DeleteMapping("/notifications/delete/{id}")
    @Operation(summary = "Supprimer une notification")
    public ResponseEntity<String> supprimerNotification(@PathVariable int id) {
        return notificationServ.supprimerNotification(id);
    }

    @DeleteMapping("/notifications/delete-all")
    @Operation(summary = "Supprimer toutes les notifications")
    public ResponseEntity<String> supprimerToutesLesNotifications() {
        return notificationServ.supprimerToutesLesNotifications();
    }

    @DeleteMapping("/notifications/delete-utilisateur/{destinataireId}")
    @Operation(summary = "Supprimer toutes les notifications d'un utilisateur")
    public ResponseEntity<String> supprimerNotificationsUtilisateur(@PathVariable int destinataireId) {
        return notificationServ.supprimerNotificationsUtilisateur(destinataireId);
    }
}
