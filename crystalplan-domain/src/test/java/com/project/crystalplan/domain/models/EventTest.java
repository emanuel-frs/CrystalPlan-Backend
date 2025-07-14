package com.project.crystalplan.domain.models;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void shouldCreateEventWithAllFields() {
        String id = "1";
        String title = "Reunião importante";
        String description = "Reunião com equipe";
        Recurrence recurrence = Recurrence.WEEKLY;
        LocalDate date = LocalDate.of(2025, 7, 10);
        Set<DayOfWeek> daysOfWeek = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        LocalTime eventTime = LocalTime.of(14, 30);
        LocalTime reminderTime = LocalTime.of(14, 0);
        boolean notify = true;
        NotificationType notificationType = NotificationType.EMAIL;
        String userId = "user-123";

        Event event = new Event(id, title, description, recurrence, date, daysOfWeek, eventTime, reminderTime, notify, notificationType, userId);

        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getTitle()).isEqualTo(title);
        assertThat(event.getDescription()).isEqualTo(description);
        assertThat(event.getRecurrence()).isEqualTo(recurrence);
        assertThat(event.getEventDate()).isEqualTo(date);
        assertThat(event.getDaysOfWeek()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        assertThat(event.getEventTime()).isEqualTo(eventTime);
        assertThat(event.getReminderTime()).isEqualTo(reminderTime);
        assertThat(event.isNotify()).isTrue();
        assertThat(event.getNotificationType()).isEqualTo(notificationType);
        assertThat(event.getUserId()).isEqualTo(userId);
    }

    @Test
    void shouldSettersAndGettersWorkCorrectly() {
        Event event = new Event();

        event.setId("2");
        event.setTitle("Consulta médica");
        event.setDescription("Consulta com dentista");
        event.setRecurrence(Recurrence.SINGLE);
        event.setEventDate(LocalDate.of(2025, 8, 5));
        event.setDaysOfWeek(Set.of(DayOfWeek.FRIDAY));
        event.setEventTime(LocalTime.of(9, 0));
        event.setReminderTime(LocalTime.of(8, 45));
        event.setNotify(false);
        event.setNotificationType(NotificationType.VISUAL);
        event.setUserId("user-456");

        assertThat(event.getId()).isEqualTo("2");
        assertThat(event.getTitle()).isEqualTo("Consulta médica");
        assertThat(event.getDescription()).isEqualTo("Consulta com dentista");
        assertThat(event.getRecurrence()).isEqualTo(Recurrence.SINGLE);
        assertThat(event.getEventDate()).isEqualTo(LocalDate.of(2025, 8, 5));
        assertThat(event.getDaysOfWeek()).containsExactly(DayOfWeek.FRIDAY);
        assertThat(event.getEventTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(event.getReminderTime()).isEqualTo(LocalTime.of(8, 45));
        assertThat(event.isNotify()).isFalse();
        assertThat(event.getNotificationType()).isEqualTo(NotificationType.VISUAL);
        assertThat(event.getUserId()).isEqualTo("user-456");
    }
}
