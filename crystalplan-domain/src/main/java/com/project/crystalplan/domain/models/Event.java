package com.project.crystalplan.domain.models;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class Event extends BaseModel{
    private String id;
    private String title;
    private String description;
    private Recurrence recurrence;
    private LocalDate eventDate;
    private Set<DayOfWeek> daysOfWeek;
    private LocalTime eventTime;
    private LocalTime reminderTime;
    private boolean notify = false;
    private NotificationType notificationType;
    private String userId;

    public Event() {}

    public Event(String id, String title, String description, Recurrence recurrence, LocalDate eventDate,
                 Set<DayOfWeek> daysOfWeek, LocalTime eventTime, LocalTime reminderTime,
                 boolean notify, NotificationType notificationType, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.recurrence = recurrence;
        this.eventDate = eventDate;
        this.daysOfWeek = daysOfWeek;
        this.eventTime = eventTime;
        this.reminderTime = reminderTime;
        this.notify = notify;
        this.notificationType = notificationType;
        this.userId = userId;
    }

    public void validate() {
        if (userId == null || userId.isBlank()) {
            throw new InvalidArgumentException("userId é obrigatório");
        }

        if (recurrence == Recurrence.SINGLE) {
            if (eventDate == null) {
                throw new InvalidArgumentException("Data do evento é obrigatória para recorrência SINGLE");
            }
        }

        if (recurrence == Recurrence.WEEKLY) {
            if (daysOfWeek == null || daysOfWeek.isEmpty()) {
                throw new InvalidArgumentException("Pelo menos um dia da semana é obrigatório para recorrência WEEKLY");
            }
            if (daysOfWeek.size() > 7) {
                throw new InvalidArgumentException("daysOfWeek não pode ter mais de 7 dias");
            }
        }

        if (notify) {
            if (eventTime == null) {
                throw new InvalidArgumentException("Horário do evento é obrigatório quando notify é true");
            }
            if (notificationType == null) {
                throw new InvalidArgumentException("notificationType deve ser informado quando notify é true");
            }
            if (reminderTime == null) {
                reminderTime = LocalTime.of(10, 0);
            }
        } else {
            notificationType = null;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Recurrence getRecurrence() { return recurrence; }
    public void setRecurrence(Recurrence recurrence) { this.recurrence = recurrence; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public Set<DayOfWeek> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) { this.daysOfWeek = daysOfWeek; }

    public LocalTime getEventTime() { return eventTime; }
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime; }

    public LocalTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalTime reminderTime) { this.reminderTime = reminderTime; }

    public boolean isNotify() { return notify; }
    public void setNotify(boolean notify) { this.notify = notify; }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
