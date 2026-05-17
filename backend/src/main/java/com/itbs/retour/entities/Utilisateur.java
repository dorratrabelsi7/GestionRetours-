package com.itbs.retour.entities;

import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String email;
    private String motDePasse;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photo;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "client")
    private List<RetourProduit> listeRetours;

    @OneToMany(mappedBy = "employe")
    private List<HistoriqueRetour> listeHistoriques;
}
