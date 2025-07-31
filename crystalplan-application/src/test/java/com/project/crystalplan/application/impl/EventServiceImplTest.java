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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
                null, // daysOfWeek must be null for SINGLE
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
                null, // eventDate must be null for WEEKLY
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
        when(eventRepository.save(any(Event.class))).thenReturn(singleEvent);

        Event createdEvent = eventService.createEvent(singleEvent);

        assertThat(createdEvent).isEqualTo(singleEvent);
        verify(eventRepository, times(1)).save(singleEvent);
    }

    @Test
    void shouldCreateWeeklyEventSuccessfully() {
        when(eventRepository.save(any(Event.class))).thenReturn(weeklyEvent);

        Event createdEvent = eventService.createEvent(weeklyEvent);

        assertThat(createdEvent).isEqualTo(weeklyEvent);
        verify(eventRepository, times(1)).save(weeklyEvent);
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingSingleEventWithoutDate() {
        Event invalidSingleEvent = new Event(
                "3", "Invalid Single Event", "Desc", Recurrence.SINGLE, null, null, null, null, false, null, "user2"
        );

        assertThrows(InvalidArgumentException.class, () -> eventService.createEvent(invalidSingleEvent));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingWeeklyEventWithoutDaysOfWeek() {
        Event invalidWeeklyEvent = new Event(
                "4", "Invalid Weekly Event", "Desc", Recurrence.WEEKLY, null, null, null, null, false, null, "user2"
        );

        assertThrows(InvalidArgumentException.class, () -> eventService.createEvent(invalidWeeklyEvent));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingWeeklyEventWithEmptyDaysOfWeek() {
        Event invalidWeeklyEvent = new Event(
                "5", "Invalid Weekly Event", "Desc", Recurrence.WEEKLY, null, Collections.emptySet(), null, null, false, null, "user2"
        );

        assertThrows(InvalidArgumentException.class, () -> eventService.createEvent(invalidWeeklyEvent));
        verify(eventRepository, never()).save(any(Event.class));
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
        when(eventRepository.findById("1")).thenReturn(Optional.of(singleEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEventDetails);

        Event result = eventService.updateEvent("1", updatedEventDetails);

        assertThat(result).isEqualTo(updatedEventDetails);
        assertThat(result.getId()).isEqualTo("1"); // Ensure ID remains the same
        verify(eventRepository, times(1)).findById("1");
        verify(eventRepository, times(1)).save(updatedEventDetails);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentEvent() {
        Event nonExistentEvent = new Event(
                "99", "Non Existent", "Desc", Recurrence.SINGLE, LocalDate.now(), null, null, null, false, null, "user1"
        );
        when(eventRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.updateEvent("99", nonExistentEvent));
        verify(eventRepository, times(1)).findById("99");
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenUpdatingSingleEventWithoutDate() {
        Event invalidUpdatedEvent = new Event(
                "1", "Updated Invalid Single Event", "Desc", Recurrence.SINGLE, null, null, null, null, false, null, "user1"
        );
        when(eventRepository.findById("1")).thenReturn(Optional.of(singleEvent));

        assertThrows(InvalidArgumentException.class, () -> eventService.updateEvent("1", invalidUpdatedEvent));
        verify(eventRepository, times(1)).findById("1");
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldGetEventByIdSuccessfully() {
        when(eventRepository.findById("1")).thenReturn(Optional.of(singleEvent));

        Optional<Event> result = eventService.getEventById("1");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(singleEvent);
        verify(eventRepository, times(1)).findById("1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentEventById() {
        when(eventRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventById("99"));
        verify(eventRepository, times(1)).findById("99");
    }

    @Test
    void shouldGetAllEventsByUser() {
        List<Event> userEvents = Arrays.asList(singleEvent, weeklyEvent);
        when(eventRepository.findByUserId("user1")).thenReturn(userEvents);

        List<Event> result = eventService.getAllEventsByUser("user1");

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(singleEvent, weeklyEvent);
        verify(eventRepository, times(1)).findByUserId("user1");
    }

    @Test
    void shouldGetSingleEventsByDate() {
        LocalDate date = LocalDate.of(2025, 7, 30);
        when(eventRepository.findByUserIdAndRecurrenceAndEventDate("user1", Recurrence.SINGLE, date))
                .thenReturn(Collections.singletonList(singleEvent));

        List<Event> result = eventService.getSingleEventsByDate("user1", date);

        assertThat(result).hasSize(1).containsExactly(singleEvent);
        verify(eventRepository, times(1)).findByUserIdAndRecurrenceAndEventDate("user1", Recurrence.SINGLE, date);
    }

    @Test
    void shouldGetWeeklyEventsByDayOfWeek() {
        DayOfWeek day = DayOfWeek.MONDAY;
        when(eventRepository.findByUserIdAndRecurrenceAndDaysOfWeekContaining("user1", Recurrence.WEEKLY, day))
                .thenReturn(Collections.singletonList(weeklyEvent));

        List<Event> result = eventService.getWeeklyEventsByDayOfWeek("user1", day);

        assertThat(result).hasSize(1).containsExactly(weeklyEvent);
        verify(eventRepository, times(1)).findByUserIdAndRecurrenceAndDaysOfWeekContaining("user1", Recurrence.WEEKLY, day);
    }

    @Test
    void shouldGetAllWeeklyEventsByUser() {
        when(eventRepository.findByUserIdAndRecurrence("user1", Recurrence.WEEKLY))
                .thenReturn(Collections.singletonList(weeklyEvent));

        List<Event> result = eventService.getAllWeeklyEventsByUser("user1");

        assertThat(result).hasSize(1).containsExactly(weeklyEvent);
        verify(eventRepository, times(1)).findByUserIdAndRecurrence("user1", Recurrence.WEEKLY);
    }

    @Test
    void shouldGetAllSingleEventsByMonth() {
        int year = 2025;
        int month = 7;
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        when(eventRepository.findByUserIdAndRecurrenceAndEventDateBetween(
                eq("user1"), eq(Recurrence.SINGLE), eq(startOfMonth), eq(endOfMonth)))
                .thenReturn(Collections.singletonList(singleEvent));

        List<Event> result = eventService.getAllSingleEventsByMonth("user1", year, month);

        assertThat(result).hasSize(1).containsExactly(singleEvent);
        verify(eventRepository, times(1))
                .findByUserIdAndRecurrenceAndEventDateBetween("user1", Recurrence.SINGLE, startOfMonth, endOfMonth);
    }

    @Test
    void shouldDeleteEventSuccessfully() {
        when(eventRepository.existsById("1")).thenReturn(true);
        doNothing().when(eventRepository).deleteById("1");

        eventService.deleteEvent("1");

        verify(eventRepository, times(1)).existsById("1");
        verify(eventRepository, times(1)).deleteById("1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.existsById("99")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventService.deleteEvent("99"));

        verify(eventRepository, times(1)).existsById("99");
        verify(eventRepository, never()).deleteById(anyString());
    }
}