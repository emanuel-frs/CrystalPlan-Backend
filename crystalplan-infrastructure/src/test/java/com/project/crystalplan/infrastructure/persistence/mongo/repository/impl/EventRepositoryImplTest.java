package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.enums.NotificationType; // Importa NotificationType
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.infrastructure.persistence.mongo.document.EventDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.EventMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataEventMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryImplTest {

    @Mock
    private SpringDataEventMongoRepository springDataRepo;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventRepositoryImpl eventRepository;

    private Event sampleEvent;
    private EventDocument sampleEventDocument;
    private String sampleUserId = "user-id-abc";

    @BeforeEach
    void setUp() {
        // Inicialização completa do Event com todos os campos
        Set<DayOfWeek> emptyDays = Collections.emptySet();
        NotificationType defaultNotificationType = NotificationType.EMAIL; // *** ALTERADO AQUI ***

        sampleEvent = new Event(
                "event-id-1",
                "Meeting",
                "Team sync up",
                Recurrence.SINGLE,
                LocalDate.of(2025, 8, 1),
                emptyDays,
                LocalTime.of(10, 0),
                LocalTime.of(9, 45),
                true,
                defaultNotificationType,
                sampleUserId
        );
        // Assumindo que EventDocument tem um construtor similar ou setters
        sampleEventDocument = new EventDocument(
                "event-id-1",
                "Meeting",
                "Team sync up",
                Recurrence.SINGLE,
                LocalDate.of(2025, 8, 1),
                emptyDays,
                LocalTime.of(10, 0),
                LocalTime.of(9, 45),
                true,
                defaultNotificationType,
                sampleUserId
        );
    }

    @Test
    void save_ShouldSaveEventAndReturnDomainObject() {
        when(mapper.toDocument(any(Event.class))).thenReturn(sampleEventDocument);
        when(springDataRepo.save(any(EventDocument.class))).thenReturn(sampleEventDocument);
        when(mapper.toDomain(any(EventDocument.class))).thenReturn(sampleEvent);

        Event savedEvent = eventRepository.save(sampleEvent);

        assertNotNull(savedEvent);
        assertEquals(sampleEvent, savedEvent);
        verify(mapper, times(1)).toDocument(sampleEvent);
        verify(springDataRepo, times(1)).save(sampleEventDocument);
        verify(mapper, times(1)).toDomain(sampleEventDocument);
    }

    @Test
    void findById_ShouldReturnEventWhenFound() {
        when(springDataRepo.findById("event-id-1")).thenReturn(Optional.of(sampleEventDocument));
        when(mapper.toDomain(any(EventDocument.class))).thenReturn(sampleEvent);

        Optional<Event> foundEvent = eventRepository.findById("event-id-1");

        assertTrue(foundEvent.isPresent());
        assertEquals(sampleEvent, foundEvent.get());
        verify(springDataRepo, times(1)).findById("event-id-1");
        verify(mapper, times(1)).toDomain(sampleEventDocument);
    }

    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<Event> foundEvent = eventRepository.findById("non-existent-id");

        assertFalse(foundEvent.isPresent());
        verify(springDataRepo, times(1)).findById("non-existent-id");
        verify(mapper, never()).toDomain(any(EventDocument.class));
    }

    @Test
    void findByUserId_ShouldReturnListOfEvents() {
        List<EventDocument> eventDocuments = Arrays.asList(sampleEventDocument, sampleEventDocument);
        List<Event> expectedEvents = Arrays.asList(sampleEvent, sampleEvent);

        when(springDataRepo.findByUserId(sampleUserId)).thenReturn(eventDocuments);
        when(mapper.toDomain(any(EventDocument.class))).thenReturn(sampleEvent);

        List<Event> foundEvents = eventRepository.findByUserId(sampleUserId);

        assertFalse(foundEvents.isEmpty());
        assertEquals(expectedEvents.size(), foundEvents.size());
        assertEquals(expectedEvents, foundEvents);
        verify(springDataRepo, times(1)).findByUserId(sampleUserId);
        verify(mapper, times(eventDocuments.size())).toDomain(any(EventDocument.class));
    }

    @Test
    void findByUserId_ShouldReturnEmptyListWhenNoneFound() {
        when(springDataRepo.findByUserId(sampleUserId)).thenReturn(Collections.emptyList());

        List<Event> foundEvents = eventRepository.findByUserId(sampleUserId);

        assertTrue(foundEvents.isEmpty());
        verify(springDataRepo, times(1)).findByUserId(sampleUserId);
        verify(mapper, never()).toDomain(any(EventDocument.class));
    }

    @Test
    void findByUserIdAndRecurrenceAndEventDate_ShouldReturnListOfEvents() {
        LocalDate eventDate = LocalDate.of(2025, 8, 1);
        Recurrence recurrence = Recurrence.SINGLE;
        List<EventDocument> eventDocuments = Collections.singletonList(sampleEventDocument);
        List<Event> expectedEvents = Collections.singletonList(sampleEvent);

        when(springDataRepo.findByUserIdAndRecurrenceAndEventDate(sampleUserId, recurrence, eventDate))
                .thenReturn(eventDocuments);
        when(mapper.toDomain(any(EventDocument.class))).thenReturn(sampleEvent);

        List<Event> foundEvents = eventRepository.findByUserIdAndRecurrenceAndEventDate(sampleUserId, recurrence, eventDate);

        assertFalse(foundEvents.isEmpty());
        assertEquals(expectedEvents.size(), foundEvents.size());
        assertEquals(expectedEvents, foundEvents);
        verify(springDataRepo, times(1)).findByUserIdAndRecurrenceAndEventDate(sampleUserId, recurrence, eventDate);
        verify(mapper, times(eventDocuments.size())).toDomain(any(EventDocument.class));
    }

    @Test
    void findByUserIdAndRecurrenceAndDaysOfWeekContaining_ShouldReturnListOfEvents() {
        Recurrence recurrence = Recurrence.WEEKLY;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        Set<DayOfWeek> specificDays = new HashSet<>(Collections.singletonList(dayOfWeek));

        Event eventForTest = new Event(
                "event-id-2", "Weekly Meeting", "Weekly team sync",
                Recurrence.WEEKLY, // Usar Weekly aqui se este for um evento semanal
                null, specificDays, LocalTime.of(9,0), LocalTime.of(8,45),
                true, NotificationType.EMAIL, sampleUserId // *** ALTERADO AQUI ***
        );
        EventDocument docForTest = new EventDocument(
                "event-id-2", "Weekly Meeting", "Weekly team sync",
                Recurrence.WEEKLY, // Usar Weekly aqui se este for um evento semanal
                null, specificDays, LocalTime.of(9,0), LocalTime.of(8,45),
                true, NotificationType.EMAIL, sampleUserId // *** ALTERADO AQUI ***
        );

        List<EventDocument> eventDocuments = Collections.singletonList(docForTest);
        List<Event> expectedEvents = Collections.singletonList(eventForTest);

        when(springDataRepo.findByUserIdAndRecurrenceAndDaysOfWeekContaining(sampleUserId, recurrence, dayOfWeek))
                .thenReturn(eventDocuments);
        when(mapper.toDomain(docForTest)).thenReturn(eventForTest);

        List<Event> foundEvents = eventRepository.findByUserIdAndRecurrenceAndDaysOfWeekContaining(sampleUserId, recurrence, dayOfWeek);

        assertFalse(foundEvents.isEmpty());
        assertEquals(expectedEvents.size(), foundEvents.size());
        assertEquals(expectedEvents, foundEvents);
        verify(springDataRepo, times(1)).findByUserIdAndRecurrenceAndDaysOfWeekContaining(sampleUserId, recurrence, dayOfWeek);
        verify(mapper, times(eventDocuments.size())).toDomain(docForTest);
    }

    @Test
    void findByUserIdAndRecurrence_ShouldReturnListOfEvents() {
        Recurrence recurrence = Recurrence.WEEKLY;
        List<EventDocument> eventDocuments = Collections.singletonList(sampleEventDocument);
        List<Event> expectedEvents = Collections.singletonList(sampleEvent);

        when(springDataRepo.findByUserIdAndRecurrence(sampleUserId, recurrence))
                .thenReturn(eventDocuments);
        when(mapper.toDomain(any(EventDocument.class))).thenReturn(sampleEvent);

        List<Event> foundEvents = eventRepository.findByUserIdAndRecurrence(sampleUserId, recurrence);

        assertFalse(foundEvents.isEmpty());
        assertEquals(expectedEvents.size(), foundEvents.size());
        assertEquals(expectedEvents, foundEvents);
        verify(springDataRepo, times(1)).findByUserIdAndRecurrence(sampleUserId, recurrence);
        verify(mapper, times(eventDocuments.size())).toDomain(any(EventDocument.class));
    }

    @Test
    void findByUserIdAndRecurrenceAndEventDateBetween_ShouldReturnListOfEvents() {
        Recurrence recurrence = Recurrence.WEEKLY;
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);
        List<EventDocument> eventDocuments = Collections.singletonList(sampleEventDocument);
        List<Event> expectedEvents = Collections.singletonList(sampleEvent);

        when(springDataRepo.findByUserIdAndRecurrenceAndEventDateBetween(sampleUserId, recurrence, startDate, endDate))
                .thenReturn(eventDocuments);
        when(mapper.toDomain(any(EventDocument.class))).thenReturn(sampleEvent);

        List<Event> foundEvents = eventRepository.findByUserIdAndRecurrenceAndEventDateBetween(sampleUserId, recurrence, startDate, endDate);

        assertFalse(foundEvents.isEmpty());
        assertEquals(expectedEvents.size(), foundEvents.size());
        assertEquals(expectedEvents, foundEvents);
        verify(springDataRepo, times(1)).findByUserIdAndRecurrenceAndEventDateBetween(sampleUserId, recurrence, startDate, endDate);
        verify(mapper, times(eventDocuments.size())).toDomain(any(EventDocument.class));
    }

    @Test
    void deleteById_ShouldCallSpringDataDeleteById() {
        doNothing().when(springDataRepo).deleteById("event-id-1");

        eventRepository.deleteById("event-id-1");

        verify(springDataRepo, times(1)).deleteById("event-id-1");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void existsById_ShouldReturnTrueWhenExists() {
        when(springDataRepo.existsById("event-id-1")).thenReturn(true);

        boolean exists = eventRepository.existsById("event-id-1");

        assertTrue(exists);
        verify(springDataRepo, times(1)).existsById("event-id-1");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void existsById_ShouldReturnFalseWhenNotExists() {
        when(springDataRepo.existsById("non-existent-id")).thenReturn(false);

        boolean exists = eventRepository.existsById("non-existent-id");

        assertFalse(exists);
        verify(springDataRepo, times(1)).existsById("non-existent-id");
        verifyNoMoreInteractions(mapper);
    }
}