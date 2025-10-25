package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_IdOrderBySentAtAsc(
            Long senderId1, Long receiverId1,
            Long senderId2, Long receiverId2
    );
}