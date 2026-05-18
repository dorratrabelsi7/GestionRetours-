package com.itbs.retour.dto;

import java.sql.Date;
import com.itbs.retour.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NotificationDTO {
    @Schema(description = "ID de la notification")
    private int id;

    @Schema(description = "Titre de la notification")
    private String titre;

    @Schema(description = "Message de la notification")
    private String message;

    @Schema(description = "Date de la notification")
    private Date date;

    @Schema(description = "Notification lue")
    private boolean lue;

    @Schema(description = "Role destinataire")
    private Role roleDestinataire;

    @Schema(description = "ID du destinataire")
    private Integer destinataireId;

    @Schema(description = "Nom du destinataire")
    private String nomDestinataire;
}
