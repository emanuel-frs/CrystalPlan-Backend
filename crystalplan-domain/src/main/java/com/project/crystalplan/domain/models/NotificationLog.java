package com.project.crystalplan.domain.models;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;

import java.time.Instant;

public class NotificationLog {
    private String id;
    private String eventId;
    private String userId;
    private NotificationType notificationType;
    private Instant sentAt;
    private NotificationStatus status;

    public NotificationLog() {}

    public NotificationLog(String id, String eventId, String userId, NotificationType notificationType,
                           Instant sentAt, NotificationStatus status) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
}
