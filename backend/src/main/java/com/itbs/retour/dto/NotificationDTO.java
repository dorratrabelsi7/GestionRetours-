package com.itbs.retour.dto;

import java.sql.Date;
import com.itbs.retour.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NotificationDTO {
    @Schema(description = "ID de la notification", example = "1")
    private int id;

    @Schema(description = "Titre de la notification", example = "Retour accepte")
    private String titre;

    @Schema(description = "Message de la notification", example = "Votre retour a ete accepte")
    private String message;

    @Schema(description = "Date de la notification", example = "2026-05-17")
    private Date date;

    @Schema(description = "Notification lue", example = "false")
    private boolean lue;

    @Schema(description = "Role destinataire", example = "CLIENT")
    private Role roleDestinataire;

    @Schema(description = "ID du destinataire", example = "1")
    private Integer destinataireId;

    @Schema(description = "Nom du destinataire", example = "Jean Dupont")
    private String nomDestinataire;
}
