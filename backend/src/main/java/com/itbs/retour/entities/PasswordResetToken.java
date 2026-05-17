package com.itbs.retour.entities;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 128)
    private String token;

    @ManyToOne(optional = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private boolean used = false;
}
