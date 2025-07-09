package com.project.crystalplan.application.services;

import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.models.NotificationLog;

import java.util.List;

public interface NotificationService {
    NotificationSettings getSettings(String userId);
    NotificationSettings saveSettings(NotificationSettings settings);
    void logNotification(NotificationLog log);
    List<NotificationLog> getLogs(String userId);
}
