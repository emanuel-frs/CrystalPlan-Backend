package com.project.crystalplan.application.impl;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.repositories.NotificationLogRepository;
import com.project.crystalplan.domain.repositories.NotificationSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationSettingsRepository settingsRepository;

    @Mock
    private NotificationLogRepository logRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationSettings defaultSettings;
    private NotificationLog sampleLog;

    @BeforeEach
    void setUp() {
        // Corrigido para corresponder ao construtor de NotificationSettings
        defaultSettings = new NotificationSettings(
                "settings-1",
                "user1",
                true,
                true,
                LocalTime.of(22, 0),
                LocalTime.of(7, 0),
                15
        );
        // Corrigido para corresponder ao construtor de NotificationLog
        sampleLog = new NotificationLog(
                "log-1",
                "event1",
                "user1",
                NotificationType.EMAIL,
                Instant.now(),
                NotificationStatus.SENDED
        );
    }

    // =========================
    // Settings Tests
    // =========================

    @Test
    void shouldGetUserSettingsSuccessfully() {
        when(settingsRepository.findByUserId("user1")).thenReturn(Optional.of(defaultSettings));

        NotificationSettings result = notificationService.getUserSettings("user1");

        assertThat(result).isEqualTo(defaultSettings);
        verify(settingsRepository, times(1)).findByUserId("user1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentUserSettings() {
        when(settingsRepository.findByUserId("nonexistentUser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.getUserSettings("nonexistentUser"));
        verify(settingsRepository, times(1)).findByUserId("nonexistentUser");
    }

    @Test
    void shouldUpdateUserSettingsSuccessfully() {
        // Corrigido para corresponder ao construtor de NotificationSettings
        NotificationSettings updatedSettings = new NotificationSettings(
                "settings-1",
                "user1",
                false,
                false,
                LocalTime.of(23, 0),
                LocalTime.of(6, 0),
                10
        );
        when(settingsRepository.save(any(NotificationSettings.class))).thenReturn(updatedSettings);

        NotificationSettings result = notificationService.updateUserSettings(updatedSettings);

        assertThat(result).isEqualTo(updatedSettings);
        verify(settingsRepository, times(1)).save(updatedSettings);
    }

    // =========================
    // Logs Tests
    // =========================

    @Test
    void shouldSaveNotificationLogSuccessfully() {
        // Mock the repository to return the same log after saving (common behavior)
        when(logRepository.save(any(NotificationLog.class))).thenReturn(sampleLog);

        notificationService.saveNotificationLog(sampleLog);

        verify(logRepository, times(1)).save(sampleLog);
    }

    @Test
    void shouldGetNotificationLogsByUserId() {
        NotificationLog anotherLog = new NotificationLog(
                "log-2", "event2", "user1", NotificationType.VISUAL, Instant.now().minusSeconds(3600), NotificationStatus.FAILURE
        );
        List<NotificationLog> userLogs = Arrays.asList(sampleLog, anotherLog);
        when(logRepository.findByUserId("user1")).thenReturn(userLogs);

        List<NotificationLog> result = notificationService.getNotificationLogsByUserId("user1");

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(sampleLog, anotherLog);
        verify(logRepository, times(1)).findByUserId("user1");
    }

    @Test
    void shouldGetNotificationLogByIdSuccessfully() {
        when(logRepository.findById("log-1")).thenReturn(Optional.of(sampleLog));

        NotificationLog result = notificationService.getNotificationLogById("log-1");

        assertThat(result).isEqualTo(sampleLog);
        verify(logRepository, times(1)).findById("log-1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentNotificationLogById() {
        when(logRepository.findById("nonexistentLog")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.getNotificationLogById("nonexistentLog"));
        verify(logRepository, times(1)).findById("nonexistentLog");
    }

    @Test
    void shouldGetLogsByEventId() {
        NotificationLog logForEvent1_1 = new NotificationLog(
                "log-e1-1", "event1", "user1", NotificationType.EMAIL, Instant.now(), NotificationStatus.SENDED
        );
        NotificationLog logForEvent1_2 = new NotificationLog(
                "log-e1-2", "event1", "user1", NotificationType.VISUAL, Instant.now().plusSeconds(60), NotificationStatus.SUCCESS
        );
        List<NotificationLog> eventLogs = Arrays.asList(logForEvent1_1, logForEvent1_2);
        when(logRepository.findByEventId("event1")).thenReturn(eventLogs);

        List<NotificationLog> result = notificationService.getLogsByEventId("event1");

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(logForEvent1_1, logForEvent1_2);
        verify(logRepository, times(1)).findByEventId("event1");
    }
}