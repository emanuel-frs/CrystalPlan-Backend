package com.project.crystalplan.domain.models;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class Event {
    private String id;
    private String title;
    private String description;
    private Recurrence recurrence;
    private LocalDate eventDate;
    private Set<DayOfWeek> daysOfWeek;
    private LocalTime eventTime;
    private LocalTime reminderTime;
    private boolean notify;
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
