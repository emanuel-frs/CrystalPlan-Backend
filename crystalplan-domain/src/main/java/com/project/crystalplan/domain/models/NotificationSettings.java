package com.project.crystalplan.domain.models;

import java.time.LocalTime;

public class NotificationSettings {
    private String id;
    private String userId;
    private Boolean emailNotificationsEnabled = true;
    private Boolean visualNotificationsEnabled = true;
    private LocalTime quietHoursStart = LocalTime.of(22, 0);
    private LocalTime quietHoursEnd = LocalTime.of(7, 0);
    private Integer defaultReminderMinutesBefore = 15;

    public NotificationSettings() {}

    public NotificationSettings(String id, String userId, Boolean emailNotificationsEnabled,
                                Boolean visualNotificationsEnabled, LocalTime quietHoursStart,
                                LocalTime quietHoursEnd, Integer defaultReminderMinutesBefore) {
        this.id = id;
        this.userId = userId;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.visualNotificationsEnabled = visualNotificationsEnabled;
        this.quietHoursStart = quietHoursStart;
        this.quietHoursEnd = quietHoursEnd;
        this.defaultReminderMinutesBefore = defaultReminderMinutesBefore;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Boolean getEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public void setEmailNotificationsEnabled(Boolean emailNotificationsEnabled) { this.emailNotificationsEnabled = emailNotificationsEnabled; }

    public Boolean getVisualNotificationsEnabled() { return visualNotificationsEnabled; }
    public void setVisualNotificationsEnabled(Boolean visualNotificationsEnabled) { this.visualNotificationsEnabled = visualNotificationsEnabled; }

    public LocalTime getQuietHoursStart() { return quietHoursStart; }
    public void setQuietHoursStart(LocalTime quietHoursStart) { this.quietHoursStart = quietHoursStart; }

    public LocalTime getQuietHoursEnd() { return quietHoursEnd; }
    public void setQuietHoursEnd(LocalTime quietHoursEnd) { this.quietHoursEnd = quietHoursEnd; }

    public Integer getDefaultReminderMinutesBefore() { return defaultReminderMinutesBefore; }
    public void setDefaultReminderMinutesBefore(Integer defaultReminderMinutesBefore) { this.defaultReminderMinutesBefore = defaultReminderMinutesBefore; }
}
