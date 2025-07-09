package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.application.services.EventService;
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

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> create(@Valid @RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity.created(URI.create("/api/events/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable String id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Event>> getAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(eventService.getAllEventsByUser(userId));
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<Event>> getSingleEventsByDate(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(eventService.getSingleEventsByDate(userId, date));
    }

    @GetMapping("/user/{userId}/day/{dayOfWeek}")
    public ResponseEntity<List<Event>> getWeeklyEventsByDayOfWeek(
            @PathVariable String userId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        return ResponseEntity.ok(eventService.getWeeklyEventsByDayOfWeek(userId, dayOfWeek));
    }

    @GetMapping("/user/{userId}/weekly")
    public ResponseEntity<List<Event>> getAllWeeklyEvents(@PathVariable String userId) {
        return ResponseEntity.ok(eventService.getAllWeeklyEventsByUser(userId));
    }

    @GetMapping("/user/{userId}/single/month/{year}/{month}")
    public ResponseEntity<List<Event>> getAllSingleEventsByMonth(
            @PathVariable String userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        return ResponseEntity.ok(eventService.getAllSingleEventsByMonth(userId, year, month));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable String id, @Valid @RequestBody Event event) {
        try {
            Event updated = eventService.updateEvent(id, event);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

