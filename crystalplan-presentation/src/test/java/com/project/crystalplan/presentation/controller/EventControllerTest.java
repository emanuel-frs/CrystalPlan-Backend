// src/test/java/com/project/crystalplan/presentation/controller/EventControllerTest.java

package com.project.crystalplan.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException; // Import necessário
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.domain.services.EventService;
import com.project.crystalplan.domain.services.UserService;
import com.project.crystalplan.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.project.crystalplan.infrastructure.security.jwt.JwtTokenProvider;
import com.project.crystalplan.presentation.exceptions.GlobalExceptionHandler; // <-- IMPORTANTE: Importar seu GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import; // <-- Importar @Import
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(
        controllers = EventController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthenticationFilter.class
        })
)
@Import(GlobalExceptionHandler.class) // <-- ADICIONE ESTA LINHA para que o handler seja carregado
@WithMockUser(username = "testuser", roles = {"USER"}) // Ajuste as roles conforme necessário
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private Event singleEvent;
    private Event weeklyEvent;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
                        .content(objectMapper.writeValueAsString(eventToCreate))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/events/new-event-id"))
                .andExpect(jsonPath("$.id", is("new-event-id")))
                .andExpect(jsonPath("$.title", is("New Event")));

        verify(eventService, times(1)).createEvent(any(Event.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingEventWithInvalidRecurrence() throws Exception {
        Event invalidEvent = new Event(
                null, "Invalid Event", "Desc", Recurrence.SINGLE, null,
                null, LocalTime.of(9, 0), LocalTime.of(8, 30), true, NotificationType.EMAIL, "user1"
        );

        // O serviço deve lançar InvalidArgumentException
        when(eventService.createEvent(any(Event.class)))
                .thenThrow(new InvalidArgumentException("Data do evento é obrigatória para recorrência SINGLE"));

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEvent))
                        .with(csrf()))
                .andExpect(status().isBadRequest()) // Espera 400
                .andExpect(jsonPath("$.status", is(400))) // Verifica o corpo da resposta
                .andExpect(jsonPath("$.error", is("Argumento inválido")))
                .andExpect(jsonPath("$.message", is("Data do evento é obrigatória para recorrência SINGLE")));

        verify(eventService, times(1)).createEvent(any(Event.class));
    }

    @Test
    void shouldGetEventById() throws Exception {
        when(eventService.getEventById("event-id-1")).thenReturn(Optional.of(singleEvent));

        mockMvc.perform(get("/api/events/event-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("event-id-1")))
                .andExpect(jsonPath("$.title", is("Single Event")));

        verify(eventService, times(1)).getEventById("event-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() throws Exception {
        // O mock do serviço retorna Optional.empty()
        when(eventService.getEventById("non-existent-id")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/non-existent-id"))
                .andExpect(status().isNotFound()) // Espera 404
                .andExpect(jsonPath("$.status", is(404))) // Confirma o status no corpo
                .andExpect(jsonPath("$.error", is("Recurso não encontrado")))
                .andExpect(jsonPath("$.message", is("Evento não encontrado com ID: non-existent-id"))); // Mensagem esperada

        verify(eventService, times(1)).getEventById("non-existent-id");
    }

    @Test
    void shouldGetAllEventsByUser() throws Exception {
        List<Event> events = Arrays.asList(singleEvent, weeklyEvent);
        when(eventService.getAllEventsByUser("user1")).thenReturn(events);

        mockMvc.perform(get("/api/events/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("event-id-1")))
                .andExpect(jsonPath("$[1].id", is("event-id-2")));

        verify(eventService, times(1)).getAllEventsByUser("user1");
    }

    @Test
    void shouldGetSingleEventsByDate() throws Exception {
        LocalDate date = LocalDate.of(2025, 10, 26);
        when(eventService.getSingleEventsByDate("user1", date)).thenReturn(List.of(singleEvent));

        mockMvc.perform(get("/api/events/user/user1/date/2025-10-26"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-1")));

        verify(eventService, times(1)).getSingleEventsByDate("user1", date);
    }

    @Test
    void shouldGetWeeklyEventsByDayOfWeek() throws Exception {
        DayOfWeek day = DayOfWeek.MONDAY;
        when(eventService.getWeeklyEventsByDayOfWeek("user1", day)).thenReturn(List.of(weeklyEvent));

        mockMvc.perform(get("/api/events/user/user1/day/MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-2")));

        verify(eventService, times(1)).getWeeklyEventsByDayOfWeek("user1", day);
    }

    @Test
    void shouldGetAllWeeklyEvents() throws Exception {
        when(eventService.getAllWeeklyEventsByUser("user1")).thenReturn(List.of(weeklyEvent));

        mockMvc.perform(get("/api/events/user/user1/weekly"))
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
        when(eventService.getAllSingleEventsByMonth("user1", 2025, 7)).thenReturn(List.of(eventInMonth));

        mockMvc.perform(get("/api/events/user/user1/single/month/2025/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("event-id-3")));

        verify(eventService, times(1)).getAllSingleEventsByMonth("user1", 2025, 7);
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        Event updatedEvent = new Event(
                "event-id-1", "Updated Title", "Updated Desc", Recurrence.SINGLE, LocalDate.of(2025, 10, 27),
                null, LocalTime.of(11, 0), LocalTime.of(10, 45), false, NotificationType.EMAIL, "user1"
        );

        when(eventService.updateEvent(eq("event-id-1"), any(Event.class))).thenReturn(updatedEvent);

        mockMvc.perform(put("/api/events/event-id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent))
                        .with(csrf()))
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
                        .content(objectMapper.writeValueAsString(nonExistentEvent))
                        .with(csrf()))
                .andExpect(status().isNotFound()) // Espera 404
                .andExpect(jsonPath("$.status", is(404))); // Confirma o status no corpo

        verify(eventService, times(1)).updateEvent(eq("non-existent-id"), any(Event.class));
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        doNothing().when(eventService).deleteEvent("event-id-1");

        mockMvc.perform(delete("/api/events/event-id-1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent("event-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentEvent() throws Exception {
        doThrow(new EntityNotFoundException("Evento não encontrado")).when(eventService).deleteEvent("non-existent-id");

        mockMvc.perform(delete("/api/events/non-existent-id")
                        .with(csrf()))
                .andExpect(status().isNotFound()) // Espera 404
                .andExpect(jsonPath("$.status", is(404))); // Confirma o status no corpo

        verify(eventService, times(1)).deleteEvent("non-existent-id");
    }
}