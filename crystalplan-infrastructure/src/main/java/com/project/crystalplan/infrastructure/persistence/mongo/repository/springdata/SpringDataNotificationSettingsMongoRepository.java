package com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata;

import com.project.crystalplan.infrastructure.persistence.mongo.document.NotificationSettingsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataNotificationSettingsMongoRepository extends MongoRepository<NotificationSettingsDocument, String> {
    Optional<NotificationSettingsDocument> findByUserId(String userId);
}
