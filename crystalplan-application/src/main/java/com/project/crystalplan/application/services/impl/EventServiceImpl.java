package com.project.crystalplan.application.services.impl;

import com.project.crystalplan.application.services.EventService;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.domain.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Event createEvent(Event event) {
        validateEventRecurrence(event);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(String id, Event updatedEvent) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));
        updatedEvent.setId(id);
        validateEventRecurrence(updatedEvent);
        return eventRepository.save(updatedEvent);
    }

    @Override
    public Optional<Event> getEventById(String id) {
        return Optional.ofNullable(eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado")));
    }

    @Override
    public List<Event> getAllEventsByUser(String userId) {
        return eventRepository.findByUserId(userId);
    }

    @Override
    public List<Event> getSingleEventsByDate(String userId, LocalDate date) {
        return eventRepository.findByUserIdAndRecurrenceAndEventDate(userId, Recurrence.SINGLE, date);
    }

    @Override
    public List<Event> getWeeklyEventsByDayOfWeek(String userId, DayOfWeek dayOfWeek) {
        return eventRepository.findByUserIdAndRecurrenceAndDaysOfWeekContaining(userId, Recurrence.WEEKLY, dayOfWeek);
    }

    @Override
    public List<Event> getAllWeeklyEventsByUser(String userId) {
        return eventRepository.findByUserIdAndRecurrence(userId, Recurrence.WEEKLY);
    }

    @Override
    public List<Event> getAllSingleEventsByMonth(String userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return eventRepository.findByUserIdAndRecurrenceAndEventDateBetween(userId, Recurrence.SINGLE, start, end);
    }

    @Override
    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Evento não encontrado");
        }
        eventRepository.deleteById(id);
    }

    private void validateEventRecurrence(Event event) {
        if (event.getRecurrence() == Recurrence.SINGLE && event.getEventDate() == null) {
            throw new InvalidArgumentException("Data do evento é obrigatória para recorrência SINGLE");
        }

        if (event.getRecurrence() == Recurrence.WEEKLY &&
                (event.getDaysOfWeek() == null || event.getDaysOfWeek().isEmpty())) {
            throw new InvalidArgumentException("Pelo menos um dia da semana é obrigatório para recorrência WEEKLY");
        }
    }
}
