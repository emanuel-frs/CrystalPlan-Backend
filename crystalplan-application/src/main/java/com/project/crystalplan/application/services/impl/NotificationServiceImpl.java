package com.project.crystalplan.application.services.impl;

import com.project.crystalplan.application.services.NotificationService;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.repositories.NotificationLogRepository;
import com.project.crystalplan.domain.repositories.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingsRepository settingsRepository;
    private final NotificationLogRepository logRepository;

    // =========================
    // Settings
    // =========================

    @Override
    public NotificationSettings getUserSettings(String userId) {
        return settingsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Configuração de notificação não encontrada para o usuário: " + userId));
    }

    @Override
    public NotificationSettings updateUserSettings(NotificationSettings settings) {
        return settingsRepository.save(settings);
    }

    // =========================
    // Logs
    // =========================

    @Override
    public void saveNotificationLog(NotificationLog log) {
        logRepository.save(log);
    }

    @Override
    public List<NotificationLog> getNotificationLogsByUserId(String userId) {
        return logRepository.findByUserId(userId);
    }

    @Override
    public NotificationLog getNotificationLogById(String logId) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Log de notificação não encontrado com ID: " + logId));
    }

    @Override
    public List<NotificationLog> getLogsByEventId(String eventId) {
        return logRepository.findByEventId(eventId);
    }
}