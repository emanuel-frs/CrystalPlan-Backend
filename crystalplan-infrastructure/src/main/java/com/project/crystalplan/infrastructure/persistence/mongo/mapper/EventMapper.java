package com.project.crystalplan.infrastructure.persistence.mongo.mapper;

import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.infrastructure.persistence.mongo.document.EventDocument;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDocument toDocument(Event event) {
        EventDocument document = new EventDocument();
        document.setId(event.getId());
        document.setUuid(event.getUuid());
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
        document.setCreatedAt(event.getCreatedAt());
        document.setUpdatedAt(event.getUpdatedAt());
        document.setActive(event.isActive());
        return document;
    }

    public Event toDomain(EventDocument doc) {
        Event event = new Event();
        event.setId(doc.getId());
        event.setUuid(doc.getUuid());
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
        event.setCreatedAt(doc.getCreatedAt());
        event.setUpdatedAt(doc.getUpdatedAt());
        event.setActive(doc.isActive());
        return event;
    }
}
