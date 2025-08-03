// presentation/controller/NotificationControllerTest.java

package com.project.crystalplan.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.services.NotificationService;
import com.project.crystalplan.domain.services.UserService;
import com.project.crystalplan.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.project.crystalplan.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = NotificationController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthenticationFilter.class
        })
)
@WithMockUser(username = "testuser", roles = {"USER"})
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private NotificationSettings settings;
    private NotificationLog log; // Este é o log que será enviado na requisição POST (sem ID gerado)
    private NotificationLog savedLog; // Este é o log que o serviço *retornará* com o ID

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        settings = new NotificationSettings(
                "settings-id-1",
                "user1",
                true,
                true,
                LocalTime.of(22, 0),
                LocalTime.of(7, 0),
                15
        );

        // 'log' representa o que seria enviado na requisição POST.
        // O ID é nulo porque, em uma requisição de criação, o cliente geralmente não envia o ID.
        log = new NotificationLog(
                null, // ID nulo para o objeto que será enviado
                "event-id-1",
                "user1",
                NotificationType.EMAIL,
                Instant.parse("2025-08-02T15:00:00Z"),
                NotificationStatus.SANDED
        );

        // 'savedLog' representa o que o serviço *retornaria* APÓS o salvamento, com o ID preenchido.
        savedLog = new NotificationLog(
                "log-id-1", // O ID que o serviço geraria/retornaria
                "event-id-1",
                "user1",
                NotificationType.EMAIL,
                Instant.parse("2025-08-02T15:00:00Z"),
                NotificationStatus.SANDED
        );
    }

    @Test
    void shouldGetUserSettings() throws Exception {
        when(notificationService.getUserSettings("user1")).thenReturn(settings);

        mockMvc.perform(get("/api/notifications/settings/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("settings-id-1")))
                .andExpect(jsonPath("$.userId", is("user1")))
                .andExpect(jsonPath("$.emailNotificationsEnabled", is(true)))
                .andExpect(jsonPath("$.visualNotificationsEnabled", is(true)));

        verify(notificationService, times(1)).getUserSettings("user1");
    }

    @Test
    void shouldSaveUserSettings() throws Exception {
        when(notificationService.updateUserSettings(any(NotificationSettings.class))).thenReturn(settings);

        mockMvc.perform(post("/api/notifications/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("settings-id-1")))
                .andExpect(jsonPath("$.userId", is("user1")));

        verify(notificationService, times(1)).updateUserSettings(any(NotificationSettings.class));
    }

    @Test
    void shouldCreateNotificationLog() throws Exception {
        // Agora o mock do serviço retorna o 'savedLog' (com ID preenchido)
        when(notificationService.saveNotificationLog(any(NotificationLog.class))).thenReturn(savedLog);

        mockMvc.perform(post("/api/notifications/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Enviamos o 'log' original (que pode ter ID nulo no request body)
                        .content(objectMapper.writeValueAsString(log))
                        .with(csrf()))
                .andExpect(status().isCreated()) // Espera 201 Created
                .andExpect(header().string("Location", "/api/notifications/logs/log-id-1")) // Header com o ID do savedLog
                .andExpect(jsonPath("$.id", is("log-id-1"))) // Corpo com o ID do savedLog
                .andExpect(jsonPath("$.userId", is("user1")));

        verify(notificationService, times(1)).saveNotificationLog(any(NotificationLog.class));
    }

    @Test
    void shouldGetLogsByUser() throws Exception {
        List<NotificationLog> logsList = Arrays.asList(savedLog); // Usa savedLog para a lista
        when(notificationService.getNotificationLogsByUserId("user1")).thenReturn(logsList);

        mockMvc.perform(get("/api/notifications/logs/user/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("log-id-1")));

        verify(notificationService, times(1)).getNotificationLogsByUserId("user1");
    }

    @Test
    void shouldGetLogById() throws Exception {
        when(notificationService.getNotificationLogById("log-id-1")).thenReturn(savedLog); // Mock retorna o savedLog

        mockMvc.perform(get("/api/notifications/logs/log-id-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("log-id-1")))
                .andExpect(jsonPath("$.userId", is("user1")));

        verify(notificationService, times(1)).getNotificationLogById("log-id-1");
    }

    @Test
    void shouldGetLogsByEvent() throws Exception {
        List<NotificationLog> logsList = Arrays.asList(savedLog); // Usa savedLog para a lista
        when(notificationService.getLogsByEventId("event-id-1")).thenReturn(logsList);

        mockMvc.perform(get("/api/notifications/logs/event/event-id-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("log-id-1")));

        verify(notificationService, times(1)).getLogsByEventId("event-id-1");
    }
}