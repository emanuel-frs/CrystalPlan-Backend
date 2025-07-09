package com.project.crystalplan.domain.repositories;

import com.project.crystalplan.domain.models.NotificationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends MongoRepository<NotificationSettings, String> {
    Optional<NotificationSettings> findByUserId(String userId);
}
