package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.domain.services.EventService;
import com.project.crystalplan.domain.models.Event;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> create(@Valid @RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity.created(URI.create("/api/events/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Event>> getById(@PathVariable String id) {
        Optional<Event> event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Event>> getAllByUser(@PathVariable String userId) {
        List<Event> events = eventService.getAllEventsByUser(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<Event>> getSingleEventsByDate(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Event> events = eventService.getSingleEventsByDate(userId, date);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/{userId}/day/{dayOfWeek}")
    public ResponseEntity<List<Event>> getWeeklyEventsByDayOfWeek(
            @PathVariable String userId,
            @PathVariable DayOfWeek dayOfWeek) {
        List<Event> events = eventService.getWeeklyEventsByDayOfWeek(userId, dayOfWeek);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/{userId}/weekly")
    public ResponseEntity<List<Event>> getAllWeeklyEvents(@PathVariable String userId) {
        List<Event> events = eventService.getAllWeeklyEventsByUser(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/{userId}/single/month/{year}/{month}")
    public ResponseEntity<List<Event>> getAllSingleEventsByMonth(
            @PathVariable String userId,
            @PathVariable int year,
            @PathVariable int month) {
        List<Event> events = eventService.getAllSingleEventsByMonth(userId, year, month);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable String id, @Valid @RequestBody Event event) {
        Event updated = eventService.updateEvent(id, event);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
