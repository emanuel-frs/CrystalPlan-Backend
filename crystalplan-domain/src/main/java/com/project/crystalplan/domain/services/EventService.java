package com.project.crystalplan.domain.services;

import com.project.crystalplan.domain.models.Event;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(Event event);
    Event updateEvent(String id, Event event);
    void deleteEvent(String id);
    Optional<Event> getEventById(String id);
    List<Event> getAllEventsByUser(String userId);
    List<Event> getSingleEventsByDate(String userId, LocalDate date);
    List<Event> getAllWeeklyEventsByUser(String userId);
    List<Event> getAllSingleEventsByMonth(String userId, int year, int month);
    List<Event> getWeeklyEventsByDayOfWeek(String userId, DayOfWeek dayOfWeek);
}

