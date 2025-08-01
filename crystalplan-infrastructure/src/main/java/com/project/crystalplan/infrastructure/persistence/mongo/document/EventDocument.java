package com.project.crystalplan.infrastructure.persistence.mongo.document;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDocument {
    @Id
    private String id;

    private String uuid;

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

}