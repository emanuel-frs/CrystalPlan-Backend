package com.project.crystalplan.infrastructure.persistence.mongo.mapper;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.infrastructure.persistence.mongo.document.EventDocument;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Set;

@Component
public class EventMapper {

    public EventDocument toDocument(Event event) {
        EventDocument document = new EventDocument();
        document.setId(event.getId());
        document.setTitle(event.getTitle());
        document.setDescription(event.getDescription());
        document.setRecurrence(event.getRecurrence());
        document.setEventDate(event.getEventDate());
        document.setDaysOfWeek(event.getDaysOfWeek());
        document.setEventTime(event.getEventTime());
        document.setReminderTime(event.getReminderTime());
        document.setNotify(event.isNotify());
        document.setNotificationType(event.getNotificationType());
        document.setUserId(event.getUserId());
        return document;
    }

    public Event toDomain(EventDocument doc) {
        Event event = new Event();
        event.setId(doc.getId());
        event.setTitle(doc.getTitle());
        event.setDescription(doc.getDescription());
        event.setRecurrence(doc.getRecurrence());
        event.setEventDate(doc.getEventDate());
        event.setDaysOfWeek(doc.getDaysOfWeek());
        event.setEventTime(doc.getEventTime());
        event.setReminderTime(doc.getReminderTime());
        event.setNotify(doc.isNotify());
        event.setNotificationType(doc.getNotificationType());
        event.setUserId(doc.getUserId());
        return event;
    }
}