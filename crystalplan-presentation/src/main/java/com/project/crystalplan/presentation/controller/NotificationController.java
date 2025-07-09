package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.application.services.NotificationService;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ==========================
    // Settings
    // ==========================

    @GetMapping("/settings/{userId}")
    public ResponseEntity<NotificationSettings> getSettings(@PathVariable String userId) {
        NotificationSettings settings = notificationService.getUserSettings(userId);
        if (settings != null) {
            return ResponseEntity.ok(settings);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/settings")
    public ResponseEntity<NotificationSettings> saveSettings(@RequestBody NotificationSettings settings) {
        NotificationSettings saved = notificationService.updateUserSettings(settings);
        return ResponseEntity.ok(saved);
    }

    // ==========================
    // Logs
    // ==========================

    @PostMapping("/logs")
    public ResponseEntity<NotificationLog> createLog(@RequestBody NotificationLog log) {
        notificationService.saveNotificationLog(log);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<NotificationLog>> getLogsByUser(@PathVariable String userId) {
        List<NotificationLog> logs = notificationService.getNotificationLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{logId}")
    public ResponseEntity<NotificationLog> getLogById(@PathVariable String logId) {
        NotificationLog log = notificationService.getNotificationLogById(logId);
        if (log != null) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/logs/event/{eventId}")
    public ResponseEntity<List<NotificationLog>> getLogsByEvent(@PathVariable String eventId) {
        List<NotificationLog> logs = notificationService.getLogsByEventId(eventId);
        return ResponseEntity.ok(logs);
    }
}
