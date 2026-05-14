package com.itbs.retour.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.ProduitStock;

public interface ProduitStockRepository extends JpaRepository<ProduitStock, Integer> {
    Optional<ProduitStock> findByNomProduitIgnoreCase(String nomProduit);
}
