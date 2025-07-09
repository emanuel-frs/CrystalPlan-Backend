package com.project.crystalplan.infraestructure.persistence.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Document(collection = "notification_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDocument {
    @Id
    private String id;
    private String userId;
    private Boolean emailNotificationsEnabled;
    private Boolean visualNotificationsEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private Integer defaultReminderMinutesBefore;
}