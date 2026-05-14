package com.itbs.retour.convertors;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itbs.retour.dto.NonConformiteDTO;
import com.itbs.retour.entities.NonConformite;

@Component
public class NonConformiteConvertor {

    @Autowired
    private ModelMapper mmapper;

    public NonConformiteDTO toDto(NonConformite nc) {
        NonConformiteDTO dto = mmapper.map(nc, NonConformiteDTO.class);
        if (nc.getRetour() != null) {
            dto.setRetourId(nc.getRetour().getId());
            dto.setProduitRetour(nc.getRetour().getProduit());
        }
        return dto;
    }

    public NonConformite fromDto(NonConformiteDTO dto) {
        return mmapper.map(dto, NonConformite.class);
    }

    public List<NonConformiteDTO> toListDto(List<NonConformite> listeNc) {
        return listeNc.stream()
                .map(nc -> toDto(nc))
                .collect(Collectors.toList());
    }
}
