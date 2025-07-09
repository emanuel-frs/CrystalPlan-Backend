package com.project.crystalplan.domain.repositories;

import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.models.Event;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(String id);
    List<Event> findByUserId(String userId);
    List<Event> findByUserIdAndRecurrenceAndEventDate(String userId, Recurrence recurrence, LocalDate eventDate);
    List<Event> findByUserIdAndRecurrenceAndDaysOfWeekContaining(String userId, Recurrence recurrence, DayOfWeek dayOfWeek);
    List<Event> findByUserIdAndRecurrence(String userId, Recurrence recurrence);
    List<Event> findByUserIdAndRecurrenceAndEventDateBetween(
            String userId,
            Recurrence recurrence,
            LocalDate startDate,
            LocalDate endDate
    );
    void deleteById(String id);
    boolean existsById(String id);
}
