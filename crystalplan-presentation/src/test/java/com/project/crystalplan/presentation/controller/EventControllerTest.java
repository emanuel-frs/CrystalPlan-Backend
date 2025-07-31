package com.project.crystalplan.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.domain.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class) // Testa apenas o EventController e seus componentes web
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc; // Usado para simular requisições HTTP

    @MockBean
    private EventService eventService; // Mocka o serviço que o controlador depende

    private ObjectMapper objectMapper; // Para converter objetos Java em JSON e vice-versa

    private Event singleEvent;
    private Event weeklyEvent;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Configura o ObjectMapper para serializar e desserializar tipos de data/hora do Java 8
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        singleEvent = new Event(
                "event-id-1",
                "Single Event",
                "Description for single",
                Recurrence.SINGLE,
                LocalDate.of(2025, 10, 26),
                null,
                LocalTime.of(10, 0),
                LocalTime.of(9, 30),
                true,
                NotificationType.EMAIL,
                "user1"
        );

        weeklyEvent = new Event(
                "event-id-2",
                "Weekly Event",
                "Description for weekly",
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
    void shouldCreateEvent() throws Exception {
        Event eventToCreate = new Event(
                null, "New Event", "New Desc", Recurrence.SINGLE, LocalDate.of(2025, 11, 1),
                null, LocalTime.of(9, 0), LocalTime.of(8, 30), true, NotificationType.EMAIL, "user1"
        );
        Event createdEvent = new Event("new-event-id", "New Event", "New Desc", Recurrence.SINGLE, LocalDate.of(2025, 11, 1),
                null, LocalTime.of(9, 0), LocalTime.of(8, 30), true, NotificationType.EMAIL, "user1"
        );

        when(eventService.createEvent(any(Event.class))).thenReturn(createdEvent);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventToCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/events/new-event-id"))
                .andExpect(jsonPath("$.id", is("new-event-id")))
                .andExpect(jsonPath("$.title", is("New Event")));

        verify(eventService, times(1)).createEvent(any(Event.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingEventWithInvalidRecurrence() throws Exception {
        // Evento SINGLE sem data (validação no serviço)
        Event invalidEvent = new Event(
                null, "Invalid Event", "Desc", Recurrence.SINGLE, null,
                null, LocalTime.of(9, 0), LocalTime.of(8, 30), true, NotificationType.EMAIL, "user1"
        );

        when(eventService.createEvent(any(Event.class)))
                .thenThrow(new InvalidArgumentException("Data do evento é obrigatória para recorrência SINGLE"));

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isBadRequest()); // Ou 422 Unprocessable Entity, dependendo da sua ExceptionHandler

        verify(eventService, times(1)).createEvent(any(Event.class));
    }


    @Test
    void shouldGetEventById() throws Exception {
        when(eventService.getEventById("event-id-1")).thenReturn(Optional.of(singleEvent));

        mockMvc.perform(get("/api/events/event-id-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("event-id-1")))
                .andExpect(jsonPath("$.title", is("Single Event")));

        verify(eventService, times(1)).getEventById("event-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() throws Exception {
        when(eventService.getEventById("non-existent-id")).thenThrow(new EntityNotFoundException("Evento não encontrado"));

        mockMvc.perform(get("/api/events/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventById("non-existent-id");
    }

    @Test
    void shouldGetAllEventsByUser() throws Exception {
        List<Event> userEvents = Arrays.asList(singleEvent, weeklyEvent);
        when(eventService.getAllEventsByUser("user1")).thenReturn(userEvents);

        mockMvc.perform(get("/api/events/user/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("event-id-1")))
                .andExpect(jsonPath("$[1].id", is("event-id-2")));

        verify(eventService, times(1)).getAllEventsByUser("user1");
    }

    @Test
    void shouldGetSingleEventsByDate() throws Exception {
        LocalDate date = LocalDate.of(2025, 10, 26);
        when(eventService.getSingleEventsByDate("user1", date)).thenReturn(Collections.singletonList(singleEvent));

        mockMvc.perform(get("/api/events/user/user1/date/2025-10-26")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-1")));

        verify(eventService, times(1)).getSingleEventsByDate("user1", date);
    }

    @Test
    void shouldGetWeeklyEventsByDayOfWeek() throws Exception {
        DayOfWeek day = DayOfWeek.MONDAY;
        when(eventService.getWeeklyEventsByDayOfWeek("user1", day)).thenReturn(Collections.singletonList(weeklyEvent));

        mockMvc.perform(get("/api/events/user/user1/day/MONDAY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-2")));

        verify(eventService, times(1)).getWeeklyEventsByDayOfWeek("user1", day);
    }

    @Test
    void shouldGetAllWeeklyEvents() throws Exception {
        when(eventService.getAllWeeklyEventsByUser("user1")).thenReturn(Collections.singletonList(weeklyEvent));

        mockMvc.perform(get("/api/events/user/user1/weekly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-2")));

        verify(eventService, times(1)).getAllWeeklyEventsByUser("user1");
    }

    @Test
    void shouldGetAllSingleEventsByMonth() throws Exception {
        Event eventInMonth = new Event(
                "event-id-3", "Monthly Event", "Desc", Recurrence.SINGLE, LocalDate.of(2025, 7, 15),
                null, LocalTime.of(12, 0), null, false, null, "user1"
        );
        when(eventService.getAllSingleEventsByMonth("user1", 2025, 7)).thenReturn(Collections.singletonList(eventInMonth));

        mockMvc.perform(get("/api/events/user/user1/single/month/2025/7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-3")));

        verify(eventService, times(1)).getAllSingleEventsByMonth("user1", 2025, 7);
    }


    @Test
    void shouldUpdateEvent() throws Exception {
        Event updatedEventDetails = new Event(
                "event-id-1", "Updated Title", "Updated Desc", Recurrence.SINGLE, LocalDate.of(2025, 10, 27),
                null, LocalTime.of(11, 0), LocalTime.of(10, 45), false, NotificationType.EMAIL, "user1"
        );

        when(eventService.updateEvent(eq("event-id-1"), any(Event.class))).thenReturn(updatedEventDetails);

        mockMvc.perform(put("/api/events/event-id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("event-id-1")))
                .andExpect(jsonPath("$.title", is("Updated Title")));

        verify(eventService, times(1)).updateEvent(eq("event-id-1"), any(Event.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentEvent() throws Exception {
        Event nonExistentEvent = new Event(
                "non-existent-id", "Non Existent", "Desc", Recurrence.SINGLE, LocalDate.now(), null, null, null, false, null, "user1"
        );
        when(eventService.updateEvent(eq("non-existent-id"), any(Event.class)))
                .thenThrow(new EntityNotFoundException("Evento não encontrado"));

        mockMvc.perform(put("/api/events/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentEvent)))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).updateEvent(eq("non-existent-id"), any(Event.class));
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        doNothing().when(eventService).deleteEvent("event-id-1");

        mockMvc.perform(delete("/api/events/event-id-1"))
                .andExpect(status().isNoContent()); // 204 No Content for successful deletion

        verify(eventService, times(1)).deleteEvent("event-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentEvent() throws Exception {
        doThrow(new EntityNotFoundException("Evento não encontrado")).when(eventService).deleteEvent("non-existent-id");

        mockMvc.perform(delete("/api/events/non-existent-id"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).deleteEvent("non-existent-id");
    }
}