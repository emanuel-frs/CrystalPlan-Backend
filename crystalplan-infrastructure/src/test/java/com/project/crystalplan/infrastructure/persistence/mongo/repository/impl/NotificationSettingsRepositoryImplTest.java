package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.models.NotificationSettings;
import com.project.crystalplan.infrastructure.persistence.mongo.document.NotificationSettingsDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.NotificationSettingsMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataNotificationSettingsMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSettingsRepositoryImplTest {

    @Mock
    private SpringDataNotificationSettingsMongoRepository springDataRepo;

    @Mock
    private NotificationSettingsMapper mapper;

    @InjectMocks
    private NotificationSettingsRepositoryImpl notificationSettingsRepository;

    private NotificationSettings sampleSettings;
    private NotificationSettingsDocument sampleSettingsDocument;
    private String sampleUserId = "user-id-xyz";
    private String sampleSettingsId = "settings-id-123";

    @BeforeEach
    void setUp() {
        sampleSettings = new NotificationSettings(
                sampleSettingsId,
                sampleUserId,
                true,
                true,
                LocalTime.of(22, 0),
                LocalTime.of(7, 0),
                15
        );
        sampleSettingsDocument = new NotificationSettingsDocument(
                sampleSettingsId,
                sampleUserId,
                true,
                true,
                LocalTime.of(22, 0),
                LocalTime.of(7, 0),
                15
        );
    }

    @Test
    void save_ShouldSaveNotificationSettingsAndReturnDomainObject() {
        when(mapper.toDocument(any(NotificationSettings.class))).thenReturn(sampleSettingsDocument);
        when(springDataRepo.save(any(NotificationSettingsDocument.class))).thenReturn(sampleSettingsDocument);
        when(mapper.toDomain(any(NotificationSettingsDocument.class))).thenReturn(sampleSettings);

        NotificationSettings savedSettings = notificationSettingsRepository.save(sampleSettings);

        assertNotNull(savedSettings);
        assertEquals(sampleSettings, savedSettings);

        verify(mapper, times(1)).toDocument(sampleSettings);
        verify(springDataRepo, times(1)).save(sampleSettingsDocument);
        verify(mapper, times(1)).toDomain(sampleSettingsDocument);
    }

    @Test
    void findById_ShouldReturnNotificationSettingsWhenFound() {
        when(springDataRepo.findById(sampleSettingsId)).thenReturn(Optional.of(sampleSettingsDocument));
        when(mapper.toDomain(any(NotificationSettingsDocument.class))).thenReturn(sampleSettings);

        Optional<NotificationSettings> foundSettings = notificationSettingsRepository.findById(sampleSettingsId);

        assertTrue(foundSettings.isPresent());
        assertEquals(sampleSettings, foundSettings.get());

        verify(springDataRepo, times(1)).findById(sampleSettingsId);
        verify(mapper, times(1)).toDomain(sampleSettingsDocument);
    }

    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<NotificationSettings> foundSettings = notificationSettingsRepository.findById("non-existent-id");

        assertFalse(foundSettings.isPresent());

        verify(springDataRepo, times(1)).findById("non-existent-id");
        verify(mapper, never()).toDomain(any(NotificationSettingsDocument.class));
    }

    @Test
    void findByUserId_ShouldReturnNotificationSettingsWhenFound() {
        when(springDataRepo.findByUserId(sampleUserId)).thenReturn(Optional.of(sampleSettingsDocument));
        when(mapper.toDomain(any(NotificationSettingsDocument.class))).thenReturn(sampleSettings);

        Optional<NotificationSettings> foundSettings = notificationSettingsRepository.findByUserId(sampleUserId);

        assertTrue(foundSettings.isPresent());
        assertEquals(sampleSettings, foundSettings.get());

        verify(springDataRepo, times(1)).findByUserId(sampleUserId);
        verify(mapper, times(1)).toDomain(sampleSettingsDocument);
    }

    @Test
    void findByUserId_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findByUserId("non-existent-user-id")).thenReturn(Optional.empty());

        Optional<NotificationSettings> foundSettings = notificationSettingsRepository.findByUserId("non-existent-user-id");

        assertFalse(foundSettings.isPresent());

        verify(springDataRepo, times(1)).findByUserId("non-existent-user-id");
        verify(mapper, never()).toDomain(any(NotificationSettingsDocument.class)); // Mapper não deve ser chamado
    }

    @Test
    void deleteById_ShouldCallSpringDataDeleteById() {
        doNothing().when(springDataRepo).deleteById(sampleSettingsId);

        notificationSettingsRepository.deleteById(sampleSettingsId);

        verify(springDataRepo, times(1)).deleteById(sampleSettingsId);
        verifyNoMoreInteractions(mapper);
    }
}