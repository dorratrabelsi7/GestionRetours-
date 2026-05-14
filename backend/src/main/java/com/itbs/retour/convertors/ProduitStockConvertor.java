package com.itbs.retour.convertors;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itbs.retour.dto.ProduitStockDTO;
import com.itbs.retour.entities.ProduitStock;

@Component
public class ProduitStockConvertor {

    @Autowired
    private ModelMapper mmapper;

    public ProduitStockDTO toDto(ProduitStock stock) {
        return mmapper.map(stock, ProduitStockDTO.class);
    }

    public ProduitStock fromDto(ProduitStockDTO dto) {
        return mmapper.map(dto, ProduitStock.class);
    }

    public List<ProduitStockDTO> toListDto(List<ProduitStock> stocks) {
        return stocks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
