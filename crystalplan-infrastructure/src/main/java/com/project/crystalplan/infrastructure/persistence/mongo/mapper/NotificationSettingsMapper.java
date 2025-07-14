package com.project.crystalplan.infrastructure.persistence.mongo.mapper;

import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.infrastructure.persistence.mongo.document.NotificationSettingsDocument;
import org.springframework.stereotype.Component;

@Component
public class NotificationSettingsMapper {

    public NotificationSettingsDocument toDocument(NotificationSettings settings) {
        if (settings == null) return null;
        return new NotificationSettingsDocument(
                settings.getId(),
                settings.getUserId(),
                settings.getEmailNotificationsEnabled(),
                settings.getVisualNotificationsEnabled(),
                settings.getQuietHoursStart(),
                settings.getQuietHoursEnd(),
                settings.getDefaultReminderMinutesBefore()
        );
    }

    public NotificationSettings toDomain(NotificationSettingsDocument document) {
        if (document == null) return null;
        return new NotificationSettings(
                document.getId(),
                document.getUserId(),
                document.getEmailNotificationsEnabled(),
                document.getVisualNotificationsEnabled(),
                document.getQuietHoursStart(),
                document.getQuietHoursEnd(),
                document.getDefaultReminderMinutesBefore()
        );
    }
}

