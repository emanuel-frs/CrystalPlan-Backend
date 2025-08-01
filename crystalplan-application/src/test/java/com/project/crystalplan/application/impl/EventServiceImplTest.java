package com.project.crystalplan.application.impl;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.domain.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event singleEvent;
    private Event weeklyEvent;

    @BeforeEach
    void setUp() {
        singleEvent = new Event(
                "1",
                "Single Event",
                "Description",
                Recurrence.SINGLE,
                LocalDate.of(2025, 7, 30),
                null,
                LocalTime.of(10, 0),
                LocalTime.of(9, 30),
                true,
                NotificationType.EMAIL,
                "user1"
        );
        weeklyEvent = new Event(
                "2",
                "Weekly Event",
                "Description",
                Recurrence.WEEKLY,
                null,
                Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                LocalTime.of(15, 0),
                LocalTime.of(14, 45),
                true,
                NotificationType.VISUAL,
                "user1"
        );
    }

    @Test
    void shouldCreateSingleEventSuccessfully() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            return e;
        });

        Event created = eventService.createEvent(singleEvent);

        assertThat(created).isEqualTo(singleEvent);
        assertThat(created.getUuid()).isNotNull();
        assertThat(created.isActive()).isTrue();
        assertThat(created.getCreatedAt()).isNotNull();
        verify(eventRepository, times(1)).save(singleEvent);
    }

    @Test
    void shouldCreateWeeklyEventSuccessfully() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event created = eventService.createEvent(weeklyEvent);

        assertThat(created).isEqualTo(weeklyEvent);
        assertThat(created.getUuid()).isNotNull();
        assertThat(created.isActive()).isTrue();
        assertThat(created.getCreatedAt()).isNotNull();
        verify(eventRepository, times(1)).save(weeklyEvent);
    }

    @Test
    void shouldUpdateEventSuccessfully() {
        Event updatedEventDetails = new Event(
                "1",
                "Updated Event",
                "Updated Description",
                Recurrence.SINGLE,
                LocalDate.of(2025, 8, 1),
                null,
                LocalTime.of(11, 0),
                LocalTime.of(10, 45),
                false,
                NotificationType.EMAIL,
                "user1"
        );
        when(eventRepository.existsById("1")).thenReturn(true);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEvent("1", updatedEventDetails);

        assertThat(result).isEqualTo(updatedEventDetails);
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(eventRepository, times(1)).existsById("1");
        verify(eventRepository, times(1)).save(updatedEventDetails);
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingSingleEventWithoutDate() {
        Event invalid = new Event("3", "Invalid", "Desc", Recurrence.SINGLE, null, null, null, null, false, null, "user2");
        assertThrows(InvalidArgumentException.class, () -> eventService.createEvent(invalid));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingWeeklyEventWithoutDaysOfWeek() {
        Event invalid = new Event("4", "Invalid", "Desc", Recurrence.WEEKLY, null, null, null, null, false, null, "user2");
        assertThrows(InvalidArgumentException.class, () -> eventService.createEvent(invalid));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingWeeklyEventWithEmptyDaysOfWeek() {
        Event invalid = new Event("5", "Invalid", "Desc", Recurrence.WEEKLY, null, Collections.emptySet(), null, null, false, null, "user2");
        assertThrows(InvalidArgumentException.class, () -> eventService.createEvent(invalid));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentEvent() {
        when(eventRepository.existsById("99")).thenReturn(false);
        Event toUpdate = new Event("99", "Non Existent", "Desc", Recurrence.SINGLE, LocalDate.now(), null, null, null, false, null, "user1");
        assertThrows(EntityNotFoundException.class, () -> eventService.updateEvent("99", toUpdate));
        verify(eventRepository, times(1)).existsById("99");
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenUpdatingSingleEventWithoutDate() {
        Event invalidUpdate = new Event("1", "Invalid", "Desc", Recurrence.SINGLE, null, null, null, null, false, null, "user1");
        when(eventRepository.existsById("1")).thenReturn(true);
        assertThrows(InvalidArgumentException.class, () -> eventService.updateEvent("1", invalidUpdate));
        verify(eventRepository, times(1)).existsById("1");
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldGetEventByIdSuccessfully() {
        when(eventRepository.findById("1")).thenReturn(Optional.of(singleEvent));
        Optional<Event> result = eventService.getEventById("1");
        assertThat(result).isPresent().contains(singleEvent);
        verify(eventRepository).findById("1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentEvent() {
        when(eventRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventService.getEventById("99"));
        verify(eventRepository).findById("99");
    }

    @Test
    void shouldGetAllEventsByUser() {
        when(eventRepository.findByUserId("user1")).thenReturn(List.of(singleEvent, weeklyEvent));
        List<Event> events = eventService.getAllEventsByUser("user1");
        assertThat(events).hasSize(2).containsExactlyInAnyOrder(singleEvent, weeklyEvent);
        verify(eventRepository).findByUserId("user1");
    }

    @Test
    void shouldGetSingleEventsByDate() {
        LocalDate date = LocalDate.of(2025, 7, 30);
        when(eventRepository.findByUserIdAndRecurrenceAndEventDate("user1", Recurrence.SINGLE, date)).thenReturn(List.of(singleEvent));
        List<Event> events = eventService.getSingleEventsByDate("user1", date);
        assertThat(events).containsExactly(singleEvent);
        verify(eventRepository).findByUserIdAndRecurrenceAndEventDate("user1", Recurrence.SINGLE, date);
    }

    @Test
    void shouldGetWeeklyEventsByDayOfWeek() {
        when(eventRepository.findByUserIdAndRecurrenceAndDaysOfWeekContaining("user1", Recurrence.WEEKLY, DayOfWeek.MONDAY)).thenReturn(List.of(weeklyEvent));
        List<Event> events = eventService.getWeeklyEventsByDayOfWeek("user1", DayOfWeek.MONDAY);
        assertThat(events).containsExactly(weeklyEvent);
        verify(eventRepository).findByUserIdAndRecurrenceAndDaysOfWeekContaining("user1", Recurrence.WEEKLY, DayOfWeek.MONDAY);
    }

    @Test
    void shouldGetAllWeeklyEventsByUser() {
        when(eventRepository.findByUserIdAndRecurrence("user1", Recurrence.WEEKLY)).thenReturn(List.of(weeklyEvent));
        List<Event> events = eventService.getAllWeeklyEventsByUser("user1");
        assertThat(events).containsExactly(weeklyEvent);
        verify(eventRepository).findByUserIdAndRecurrence("user1", Recurrence.WEEKLY);
    }

    @Test
    void shouldGetAllSingleEventsByMonth() {
        int year = 2025, month = 7;
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        when(eventRepository.findByUserIdAndRecurrenceAndEventDateBetween("user1", Recurrence.SINGLE, start, end)).thenReturn(List.of(singleEvent));
        List<Event> events = eventService.getAllSingleEventsByMonth("user1", year, month);
        assertThat(events).containsExactly(singleEvent);
        verify(eventRepository).findByUserIdAndRecurrenceAndEventDateBetween("user1", Recurrence.SINGLE, start, end);
    }

    @Test
    void shouldDeleteEventSuccessfully() {
        when(eventRepository.existsById("1")).thenReturn(true);
        eventService.deleteEvent("1");
        verify(eventRepository).existsById("1");
        verify(eventRepository).deleteById("1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.existsById("99")).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> eventService.deleteEvent("99"));
        verify(eventRepository).existsById("99");
        verify(eventRepository, never()).deleteById(any());
    }
}