package com.itbs.retour.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.NonConformite;
import com.itbs.retour.entities.Gravite;

public interface NonConformiteRepository extends JpaRepository<NonConformite, Integer> {
    List<NonConformite> findByGravite(Gravite gravite);
    List<NonConformite> findByRetourId(int retourId);
    void deleteByRetourId(int retourId);
}
