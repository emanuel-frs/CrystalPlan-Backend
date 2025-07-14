package com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata;

import com.project.crystalplan.infrastructure.persistence.mongo.document.EventDocument;
import com.project.crystalplan.domain.enums.Recurrence;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface SpringDataEventMongoRepository extends MongoRepository<EventDocument, String> {
    List<EventDocument> findByUserId(String userId);
    List<EventDocument> findByUserIdAndRecurrenceAndEventDate(String userId, Recurrence recurrence, LocalDate eventDate);
    List<EventDocument> findByUserIdAndRecurrenceAndDaysOfWeekContaining(String userId, Recurrence recurrence, DayOfWeek dayOfWeek);
    List<EventDocument> findByUserIdAndRecurrence(String userId, Recurrence recurrence);
    List<EventDocument> findByUserIdAndRecurrenceAndEventDateBetween(
            String userId,
            Recurrence recurrence,
            LocalDate startDate,
            LocalDate endDate
    );
}
