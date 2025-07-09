package com.project.crystalplan.infraestructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.domain.repositories.EventRepository;
import com.project.crystalplan.infraestructure.persistence.mongo.repository.springdata.SpringDataEventMongoRepository;
import com.project.crystalplan.infraestructure.persistence.mongo.document.EventDocument;
import com.project.crystalplan.infraestructure.persistence.mongo.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private final SpringDataEventMongoRepository springDataRepo;
    private final EventMapper mapper;

    @Autowired
    public EventRepositoryImpl(SpringDataEventMongoRepository springDataRepo, EventMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public Event save(Event event) {
        EventDocument document = mapper.toDocument(event);
        EventDocument saved = springDataRepo.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Event> findById(String id) {
        return springDataRepo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Event> findByUserId(String userId) {
        return springDataRepo.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByUserIdAndRecurrenceAndEventDate(String userId, Recurrence recurrence, LocalDate eventDate) {
        return springDataRepo.findByUserIdAndRecurrenceAndEventDate(userId, recurrence, eventDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByUserIdAndRecurrenceAndDaysOfWeekContaining(String userId, Recurrence recurrence, DayOfWeek dayOfWeek) {
        return springDataRepo.findByUserIdAndRecurrenceAndDaysOfWeekContaining(userId, recurrence, dayOfWeek).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByUserIdAndRecurrence(String userId, Recurrence recurrence) {
        return springDataRepo.findByUserIdAndRecurrence(userId, recurrence).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByUserIdAndRecurrenceAndEventDateBetween(String userId, Recurrence recurrence, LocalDate startDate, LocalDate endDate) {
        return springDataRepo.findByUserIdAndRecurrenceAndEventDateBetween(userId, recurrence, startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        springDataRepo.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return springDataRepo.existsById(id);
    }
}
