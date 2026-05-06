package com.itbs.retour.repositories;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.RetourProduit;
import com.itbs.retour.entities.EtatTraitement;

public interface RetourProduitRepository extends JpaRepository<RetourProduit, Integer> {
    List<RetourProduit> findByEtatTraitement(EtatTraitement etat);
    List<RetourProduit> findByClientId(int clientId);
    List<RetourProduit> findByDateBetween(Date dateDebut, Date dateFin);
}