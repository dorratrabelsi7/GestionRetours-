package com.itbs.retour.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itbs.retour.entities.Notification;
import com.itbs.retour.entities.Role;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByDestinataireId(int destinataireId);
    List<Notification> findByRoleDestinataire(Role roleDestinataire);
    long countByDestinataireIdAndLueFalse(int destinataireId);
    long countByRoleDestinataireAndLueFalse(Role roleDestinataire);
}
