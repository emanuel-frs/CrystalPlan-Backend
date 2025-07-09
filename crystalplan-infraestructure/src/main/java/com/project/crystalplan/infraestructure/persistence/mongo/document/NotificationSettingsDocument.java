package com.project.crystalplan.infraestructure.persistence.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.UUID;

@Document(collection = "notification_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDocument {
    @Id
    private String id = UUID.randomUUID().toString();

    private String userId;
    private Boolean emailNotificationsEnabled = true;
    private Boolean visualNotificationsEnabled = true;
    private LocalTime quietHoursStart = LocalTime.of(22, 0);
    private LocalTime quietHoursEnd = LocalTime.of(7, 0);
    private Integer defaultReminderMinutesBefore = 15;
}
