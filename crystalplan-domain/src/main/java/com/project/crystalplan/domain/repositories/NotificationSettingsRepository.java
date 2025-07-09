package com.project.crystalplan.domain.repositories;

import com.project.crystalplan.domain.models.NotificationSettings;

import java.util.Optional;

public interface NotificationSettingsRepository {
    NotificationSettings save(NotificationSettings settings);
    Optional<NotificationSettings> findById(String id);
    Optional<NotificationSettings> findByUserId(String userId);
    void deleteById(String id);
}