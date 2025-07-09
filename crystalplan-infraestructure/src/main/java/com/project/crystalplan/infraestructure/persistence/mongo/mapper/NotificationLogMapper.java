package com.project.crystalplan.infraestructure.persistence.mongo.mapper;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.infraestructure.persistence.mongo.document.NotificationLogDocument;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NotificationLogMapper {

    public NotificationLogDocument toDocument(NotificationLog notificationLog) {
        NotificationLogDocument document = new NotificationLogDocument();
        document.setId(notificationLog.getId());
        document.setEventId(notificationLog.getEventId());
        document.setUserId(notificationLog.getUserId());
        document.setNotificationType(notificationLog.getNotificationType());
        document.setSentAt(notificationLog.getSentAt());
        document.setStatus(notificationLog.getStatus());
        return document;
    }

    public NotificationLog toDomain(NotificationLogDocument doc) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setId(doc.getId());
        notificationLog.setEventId(doc.getEventId());
        notificationLog.setUserId(doc.getUserId());
        notificationLog.setNotificationType(doc.getNotificationType());
        notificationLog.setSentAt(doc.getSentAt());
        notificationLog.setStatus(doc.getStatus());
        return notificationLog;
    }
}