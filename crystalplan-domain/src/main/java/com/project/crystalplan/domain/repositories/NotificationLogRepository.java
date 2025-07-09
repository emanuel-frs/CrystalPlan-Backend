package com.project.crystalplan.domain.repositories;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.models.NotificationLog;
import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository {
    NotificationLog save(NotificationLog notificationLog);
    Optional<NotificationLog> findById(String id);
    List<NotificationLog> findByUserId(String userId);
    List<NotificationLog> findByEventId(String eventId);
    List<NotificationLog> findByStatus(NotificationStatus status);
    void deleteById(String id);
}