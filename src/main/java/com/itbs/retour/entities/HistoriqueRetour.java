package com.itbs.retour.entities;

import java.sql.Date;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class HistoriqueRetour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String action;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "retour_id")
    private RetourProduit retour;

    @ManyToOne
    @JoinColumn(name = "employe_id")
    private Utilisateur employe;
}