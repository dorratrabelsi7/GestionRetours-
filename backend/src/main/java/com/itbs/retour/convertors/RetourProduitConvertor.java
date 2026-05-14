package com.itbs.retour.convertors;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itbs.retour.dto.RetourProduitDTO;
import com.itbs.retour.entities.RetourProduit;

@Component
public class RetourProduitConvertor {

    @Autowired
    private ModelMapper mmapper;

    public RetourProduitDTO toDto(RetourProduit r) {
        RetourProduitDTO dto = mmapper.map(r, RetourProduitDTO.class);
        if (r.getClient() != null) {
            dto.setClientId(r.getClient().getId());
            dto.setNomClient(r.getClient().getNom());
        }
        return dto;
    }

    public RetourProduit fromDto(RetourProduitDTO dto) {
        return mmapper.map(dto, RetourProduit.class);
    }

    public List<RetourProduitDTO> toListDto(List<RetourProduit> listeR) {
        return listeR.stream()
                .map(r -> toDto(r))
                .collect(Collectors.toList());
    }
}
