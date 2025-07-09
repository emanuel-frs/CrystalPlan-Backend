package com.project.crystalplan.application.services.impl;

import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.repositories.NotificationLogRepository;
import com.project.crystalplan.domain.repositories.NotificationSettingsRepository;
import com.project.crystalplan.application.services.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingsRepository settingsRepo;
    private final NotificationLogRepository logRepo;

    public NotificationServiceImpl(NotificationSettingsRepository settingsRepo, NotificationLogRepository logRepo) {
        this.settingsRepo = settingsRepo;
        this.logRepo = logRepo;
    }

    @Override
    public NotificationSettings getSettings(String userId) {
        return settingsRepo.findByUserId(userId).orElse(null);
    }

    @Override
    public NotificationSettings saveSettings(NotificationSettings settings) {
        return settingsRepo.save(settings);
    }

    @Override
    public void logNotification(NotificationLog log) {
        logRepo.save(log);
    }

    @Override
    public List<NotificationLog> getLogs(String userId) {
        return logRepo.findByUserId(userId);
    }
}
