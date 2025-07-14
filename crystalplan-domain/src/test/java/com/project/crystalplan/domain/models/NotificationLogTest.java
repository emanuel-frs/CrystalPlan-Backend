package com.project.crystalplan.domain.models;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationLogTest {

    @Test
    void shouldCreateNotificationLogWithAllFields() {
        String id = "log-001";
        String eventId = "event-001";
        String userId = "user-001";
        NotificationType type = NotificationType.EMAIL;
        Instant sentAt = Instant.now();
        NotificationStatus status = NotificationStatus.SUCCESS;

        NotificationLog log = new NotificationLog(id, eventId, userId, type, sentAt, status);

        assertThat(log.getId()).isEqualTo(id);
        assertThat(log.getEventId()).isEqualTo(eventId);
        assertThat(log.getUserId()).isEqualTo(userId);
        assertThat(log.getNotificationType()).isEqualTo(type);
        assertThat(log.getSentAt()).isEqualTo(sentAt);
        assertThat(log.getStatus()).isEqualTo(status);
    }

    @Test
    void shouldSettersAndGettersWorkCorrectly() {
        NotificationLog log = new NotificationLog();

        String id = "log-002";
        String eventId = "event-002";
        String userId = "user-002";
        NotificationType type = NotificationType.VISUAL;
        Instant sentAt = Instant.now();
        NotificationStatus status = NotificationStatus.FAILURE;

        log.setId(id);
        log.setEventId(eventId);
        log.setUserId(userId);
        log.setNotificationType(type);
        log.setSentAt(sentAt);
        log.setStatus(status);

        assertThat(log.getId()).isEqualTo(id);
        assertThat(log.getEventId()).isEqualTo(eventId);
        assertThat(log.getUserId()).isEqualTo(userId);
        assertThat(log.getNotificationType()).isEqualTo(type);
        assertThat(log.getSentAt()).isEqualTo(sentAt);
        assertThat(log.getStatus()).isEqualTo(status);
    }
}
