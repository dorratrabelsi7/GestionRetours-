package com.itbs.retour.convertors;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itbs.retour.dto.NotificationDTO;
import com.itbs.retour.entities.Notification;

@Component
public class NotificationConvertor {

    @Autowired
    private ModelMapper mmapper;

    public NotificationDTO toDto(Notification notification) {
        NotificationDTO dto = mmapper.map(notification, NotificationDTO.class);
        if (notification.getDestinataire() != null) {
            dto.setDestinataireId(notification.getDestinataire().getId());
            dto.setNomDestinataire(notification.getDestinataire().getNom());
        }
        return dto;
    }

    public List<NotificationDTO> toListDto(List<Notification> listeNotifications) {
        return listeNotifications.stream()
                .map(notification -> toDto(notification))
                .collect(Collectors.toList());
    }
}
