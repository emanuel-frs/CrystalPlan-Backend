package com.project.crystalplan.infraestructure.persistence.mongo.repository.springdata;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.infraestructure.persistence.mongo.document.NotificationLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataNotificationLogMongoRepository extends MongoRepository<NotificationLogDocument, String> {
    List<NotificationLogDocument> findByUserId(String userId);
    List<NotificationLogDocument> findByEventId(String eventId);
    List<NotificationLogDocument> findByStatus(NotificationStatus status);
}