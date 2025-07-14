package com.project.crystalplan.domain.services;

import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.models.NotificationLog;

import java.util.List;

public interface NotificationService {

    // Settings
    NotificationSettings getUserSettings(String userId);
    NotificationSettings updateUserSettings(NotificationSettings settings);

    // Logs
    void saveNotificationLog(NotificationLog log);
    List<NotificationLog> getNotificationLogsByUserId(String userId);
    NotificationLog getNotificationLogById(String logId);
    List<NotificationLog> getLogsByEventId(String eventId);
}
