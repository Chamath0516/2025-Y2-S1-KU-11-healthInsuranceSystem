package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.service.NotificationService;
import com.hims.healthinsurancesystem.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationService.save(notification);
        return ResponseEntity.ok(savedNotification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.findAll();
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        if (notificationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Messaging APIs
    @GetMapping("/conversation")
    public ResponseEntity<List<Notification>> getConversation(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        return ResponseEntity.ok(notificationService.getConversation(user1Id, user2Id));
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam String message) {
        return notificationService.sendMessage(senderId, receiverId, message)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Invalid sender/receiver/message"));
    }
}