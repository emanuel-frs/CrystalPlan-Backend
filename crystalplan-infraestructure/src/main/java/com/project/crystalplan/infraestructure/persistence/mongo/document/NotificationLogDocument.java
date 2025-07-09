package com.project.crystalplan.infraestructure.persistence.mongo.document;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogDocument {
    @Id
    private String id = UUID.randomUUID().toString();
    private String eventId;
    private String userId;
    private NotificationType notificationType;
    private Instant sentAt;
    private NotificationStatus status;
}