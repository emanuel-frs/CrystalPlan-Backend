package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.domain.services.NotificationService;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ==========================
    // Settings
    // ==========================

    @GetMapping("/settings/{userId}")
    public ResponseEntity<NotificationSettings> getSettings(@PathVariable String userId) {
        NotificationSettings settings = notificationService.getUserSettings(userId);
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/settings")
    public ResponseEntity<NotificationSettings> saveSettings(@Valid @RequestBody NotificationSettings settings) {
        NotificationSettings saved = notificationService.updateUserSettings(settings);
        return ResponseEntity.ok(saved);
    }

    // ==========================
    // Logs
    // ==========================

    @PostMapping("/logs")
    public ResponseEntity<NotificationLog> createLog(@Valid @RequestBody NotificationLog log) {
        notificationService.saveNotificationLog(log);
        return ResponseEntity.created(URI.create("/api/notifications/logs/" + log.getId())).body(log);
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<NotificationLog>> getLogsByUser(@PathVariable String userId) {
        List<NotificationLog> logs = notificationService.getNotificationLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{logId}")
    public ResponseEntity<NotificationLog> getLogById(@PathVariable String logId) {
        NotificationLog log = notificationService.getNotificationLogById(logId);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/logs/event/{eventId}")
    public ResponseEntity<List<NotificationLog>> getLogsByEvent(@PathVariable String eventId) {
        List<NotificationLog> logs = notificationService.getLogsByEventId(eventId);
        return ResponseEntity.ok(logs);
    }
}