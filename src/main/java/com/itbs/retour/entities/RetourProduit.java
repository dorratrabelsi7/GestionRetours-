package com.itbs.retour.entities;

import java.sql.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RetourProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String produit;
    private String raison;

    @Enumerated(EnumType.STRING)
    private EtatTraitement etatTraitement;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Utilisateur client;

    @OneToMany(mappedBy = "retour")
    private List<NonConformite> listeNonConformites;

    @OneToMany(mappedBy = "retour")
    private List<HistoriqueRetour> listeHistoriques;
}