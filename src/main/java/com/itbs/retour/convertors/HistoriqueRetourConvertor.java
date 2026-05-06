package com.itbs.retour.convertors;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itbs.retour.dto.HistoriqueRetourDTO;
import com.itbs.retour.entities.HistoriqueRetour;

@Component
public class HistoriqueRetourConvertor {

    @Autowired
    private ModelMapper mmapper;

    public HistoriqueRetourDTO toDto(HistoriqueRetour h) {
        HistoriqueRetourDTO dto = mmapper.map(h, HistoriqueRetourDTO.class);
        dto.setProduitRetour(h.getRetour().getProduit());  // champ aplati
        dto.setNomEmploye(h.getEmploye().getNom());        // champ aplati
        return dto;
    }

    public HistoriqueRetour fromDto(HistoriqueRetourDTO dto) {
        return mmapper.map(dto, HistoriqueRetour.class);
    }

    public List<HistoriqueRetourDTO> toListDto(List<HistoriqueRetour> listeH) {
        return listeH.stream()
                .map(h -> toDto(h))
                .collect(Collectors.toList());
    }
}