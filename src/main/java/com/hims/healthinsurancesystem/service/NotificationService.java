package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Notification;
import com.hims.healthinsurancesystem.model.User;
import com.hims.healthinsurancesystem.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    public List<Notification> getConversation(Long user1Id, Long user2Id) {
        return notificationRepository
                .findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_IdOrderBySentAtAsc(user1Id, user2Id, user2Id, user1Id);
    }

    public Optional<Notification> sendMessage(Long senderId, Long receiverId, String message) {
        if (senderId == null || receiverId == null || message == null || message.isBlank()) {
            return Optional.empty();
        }
        Optional<User> senderOpt = userService.findById(senderId);
        Optional<User> receiverOpt = userService.findById(receiverId);
        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return Optional.empty();
        }
        Notification n = new Notification();
        n.setSender(senderOpt.get());
        n.setReceiver(receiverOpt.get());
        n.setMessage(message.trim());
        n.setType("CHAT");
        return Optional.of(notificationRepository.save(n));
    }
}