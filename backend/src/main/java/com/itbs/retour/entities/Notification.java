package com.itbs.retour.entities;

import java.sql.Date;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private String message;
    private Date date;
    private boolean lue;

    @Enumerated(EnumType.STRING)
    private Role roleDestinataire;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;
}
