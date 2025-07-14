package com.project.crystalplan.domain.models;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationSettingsTest {

    @Test
    void shouldCreateNotificationSettingsWithAllFields() {
        String id = "settings-001";
        String userId = "user-001";
        Boolean emailEnabled = false;
        Boolean visualEnabled = true;
        LocalTime quietStart = LocalTime.of(23, 0);
        LocalTime quietEnd = LocalTime.of(6, 0);
        Integer reminderMinutes = 10;

        NotificationSettings settings = new NotificationSettings(
                id, userId, emailEnabled, visualEnabled, quietStart, quietEnd, reminderMinutes
        );

        assertThat(settings.getId()).isEqualTo(id);
        assertThat(settings.getUserId()).isEqualTo(userId);
        assertThat(settings.getEmailNotificationsEnabled()).isEqualTo(emailEnabled);
        assertThat(settings.getVisualNotificationsEnabled()).isEqualTo(visualEnabled);
        assertThat(settings.getQuietHoursStart()).isEqualTo(quietStart);
        assertThat(settings.getQuietHoursEnd()).isEqualTo(quietEnd);
        assertThat(settings.getDefaultReminderMinutesBefore()).isEqualTo(reminderMinutes);
    }

    @Test
    void shouldSettersAndGettersWorkCorrectly() {
        NotificationSettings settings = new NotificationSettings();

        String id = "settings-002";
        String userId = "user-002";
        Boolean emailEnabled = true;
        Boolean visualEnabled = false;
        LocalTime quietStart = LocalTime.of(21, 0);
        LocalTime quietEnd = LocalTime.of(8, 0);
        Integer reminderMinutes = 20;

        settings.setId(id);
        settings.setUserId(userId);
        settings.setEmailNotificationsEnabled(emailEnabled);
        settings.setVisualNotificationsEnabled(visualEnabled);
        settings.setQuietHoursStart(quietStart);
        settings.setQuietHoursEnd(quietEnd);
        settings.setDefaultReminderMinutesBefore(reminderMinutes);

        assertThat(settings.getId()).isEqualTo(id);
        assertThat(settings.getUserId()).isEqualTo(userId);
        assertThat(settings.getEmailNotificationsEnabled()).isEqualTo(emailEnabled);
        assertThat(settings.getVisualNotificationsEnabled()).isEqualTo(visualEnabled);
        assertThat(settings.getQuietHoursStart()).isEqualTo(quietStart);
        assertThat(settings.getQuietHoursEnd()).isEqualTo(quietEnd);
        assertThat(settings.getDefaultReminderMinutesBefore()).isEqualTo(reminderMinutes);
    }
}
