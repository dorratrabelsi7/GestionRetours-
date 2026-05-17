package com.itbs.retour.repositories;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.HistoriqueRetour;

public interface HistoriqueRetourRepository extends JpaRepository<HistoriqueRetour, Integer> {
    List<HistoriqueRetour> findByRetourId(int retourId);
    List<HistoriqueRetour> findByEmployeId(int employeId);
    List<HistoriqueRetour> findByDateBetween(Date dateDebut, Date dateFin);
    void deleteByRetourId(int retourId);
}
