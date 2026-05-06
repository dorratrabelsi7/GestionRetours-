package com.itbs.retour.convertors;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itbs.retour.dto.UtilisateurDTO;
import com.itbs.retour.entities.Utilisateur;

@Component
public class UtilisateurConvertor {

    @Autowired
    private ModelMapper mmapper;

    public UtilisateurDTO toDto(Utilisateur u) {
        UtilisateurDTO dto = mmapper.map(u, UtilisateurDTO.class);
        return dto;
    }

    public Utilisateur fromDto(UtilisateurDTO dto) {
        return mmapper.map(dto, Utilisateur.class);
    }

    public List<UtilisateurDTO> toListDto(List<Utilisateur> listeU) {
        return listeU.stream()
                .map(u -> toDto(u))
                .collect(Collectors.toList());
    }
}