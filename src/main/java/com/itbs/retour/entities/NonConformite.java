package com.itbs.retour.entities;

import java.sql.Date;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class NonConformite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Gravite gravite;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "retour_id")
    private RetourProduit retour;
}