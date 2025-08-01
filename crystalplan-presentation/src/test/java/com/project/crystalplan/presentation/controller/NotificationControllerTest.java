package com.project.crystalplan.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.domain.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private ObjectMapper objectMapper;

    private NotificationSettings defaultSettings;
    private NotificationLog sampleLog;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        defaultSettings = new NotificationSettings(
                "settings-id-1", "user1", true, true, LocalTime.of(22, 0), LocalTime.of(7, 0), 15
        );
        sampleLog = new NotificationLog(
                "log-id-1", "event1", "user1", NotificationType.EMAIL, Instant.now(), NotificationStatus.SANDED
        );
    }

    // ==========================
    // Settings Tests
    // ==========================

    @Test
    void shouldGetSettingsByUserId() throws Exception {
        when(notificationService.getUserSettings("user1")).thenReturn(defaultSettings);

        mockMvc.perform(get("/api/notifications/settings/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("settings-id-1")))
                .andExpect(jsonPath("$.userId", is("user1")));

        verify(notificationService, times(1)).getUserSettings("user1");
    }

    @Test
    void shouldReturnNotFoundWhenSettingsDoesNotExist() throws Exception {
        when(notificationService.getUserSettings("nonexistentUser"))
                .thenThrow(new EntityNotFoundException("Configuração de notificação não encontrada"));

        mockMvc.perform(get("/api/notifications/settings/nonexistentUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getUserSettings("nonexistentUser");
    }

    @Test
    void shouldSaveSettings() throws Exception {
        NotificationSettings newSettings = new NotificationSettings(
                null, "user2", false, true, LocalTime.of(23, 0), LocalTime.of(6, 0), 30
        );
        NotificationSettings savedSettings = new NotificationSettings(
                "settings-id-2", "user2", false, true, LocalTime.of(23, 0), LocalTime.of(6, 0), 30
        );

        when(notificationService.updateUserSettings(any(NotificationSettings.class))).thenReturn(savedSettings);

        mockMvc.perform(post("/api/notifications/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSettings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("settings-id-2")))
                .andExpect(jsonPath("$.userId", is("user2")));

        verify(notificationService, times(1)).updateUserSettings(any(NotificationSettings.class));
    }

    // ==========================
    // Logs Tests
    // ==========================

    @Test
    void shouldCreateNotificationLog() throws Exception {
        NotificationLog logToCreate = new NotificationLog(
                "new-log-id", "event2", "user1", NotificationType.VISUAL, Instant.now(), NotificationStatus.SANDED
        );

        // O serviço saveNotificationLog é void, então usamos doNothing
        doNothing().when(notificationService).saveNotificationLog(any(NotificationLog.class));

        mockMvc.perform(post("/api/notifications/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logToCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/notifications/logs/new-log-id"))
                .andExpect(jsonPath("$.id", is("new-log-id")));

        verify(notificationService, times(1)).saveNotificationLog(any(NotificationLog.class));
    }

    @Test
    void shouldGetLogsByUserId() throws Exception {
        NotificationLog logForUser1_2 = new NotificationLog(
                "log-id-2", "event3", "user1", NotificationType.EMAIL, Instant.now(), NotificationStatus.FAILURE
        );
        List<NotificationLog> userLogs = Arrays.asList(sampleLog, logForUser1_2);
        when(notificationService.getNotificationLogsByUserId("user1")).thenReturn(userLogs);

        mockMvc.perform(get("/api/notifications/logs/user/user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("log-id-1")))
                .andExpect(jsonPath("$[1].id", is("log-id-2")));

        verify(notificationService, times(1)).getNotificationLogsByUserId("user1");
    }

    @Test
    void shouldGetLogById() throws Exception {
        when(notificationService.getNotificationLogById("log-id-1")).thenReturn(sampleLog);

        mockMvc.perform(get("/api/notifications/logs/log-id-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("log-id-1")))
                .andExpect(jsonPath("$.eventId", is("event1")));

        verify(notificationService, times(1)).getNotificationLogById("log-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenLogDoesNotExist() throws Exception {
        when(notificationService.getNotificationLogById("nonexistentLog"))
                .thenThrow(new EntityNotFoundException("Log de notificação não encontrado"));

        mockMvc.perform(get("/api/notifications/logs/nonexistentLog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationLogById("nonexistentLog");
    }

    @Test
    void shouldGetLogsByEventId() throws Exception {
        NotificationLog logForEvent1_2 = new NotificationLog(
                "log-id-3", "event1", "user2", NotificationType.EMAIL, Instant.now().minusSeconds(100), NotificationStatus.SANDED
        );
        List<NotificationLog> eventLogs = Arrays.asList(sampleLog, logForEvent1_2);
        when(notificationService.getLogsByEventId("event1")).thenReturn(eventLogs);

        mockMvc.perform(get("/api/notifications/logs/event/event1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("log-id-1")))
                .andExpect(jsonPath("$[1].id", is("log-id-3")));

        verify(notificationService, times(1)).getLogsByEventId("event1");
    }
}