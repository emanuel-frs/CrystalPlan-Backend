package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.application.services.NotificationService;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/settings/{userId}")
    public NotificationSettings getSettings(@PathVariable String userId) {
        return service.getSettings(userId);
    }

    @PostMapping("/settings")
    public NotificationSettings saveSettings(@RequestBody NotificationSettings settings) {
        return service.saveSettings(settings);
    }

    @PostMapping("/log")
    public void logNotification(@RequestBody NotificationLog log) {
        service.logNotification(log);
    }

    @GetMapping("/logs/{userId}")
    public List<NotificationLog> getLogs(@PathVariable String userId) {
        return service.getLogs(userId);
    }
}

