package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.enums.NotificationStatus;
import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.models.NotificationLog;
import com.project.crystalplan.infrastructure.persistence.mongo.document.NotificationLogDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.NotificationLogMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataNotificationLogMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationLogRepositoryImplTest {

    @Mock
    private SpringDataNotificationLogMongoRepository springDataRepo;

    @Mock
    private NotificationLogMapper mapper;

    @InjectMocks
    private NotificationLogRepositoryImpl notificationLogRepository;

    private NotificationLog sampleLog;
    private NotificationLogDocument sampleLogDocument;
    private String sampleUserId = "user-id-xyz";
    private String sampleEventId = "event-id-abc";

    @BeforeEach
    void setUp() {
        sampleLog = new NotificationLog(
                "log-id-1",
                sampleEventId,
                sampleUserId,
                NotificationType.EMAIL,
                Instant.now(),
                NotificationStatus.SANDED
        );
        sampleLogDocument = new NotificationLogDocument(
                "log-id-1",
                sampleEventId,
                sampleUserId,
                NotificationType.EMAIL,
                Instant.now(),
                NotificationStatus.SANDED
        );
    }

    @Test
    void save_ShouldSaveNotificationLogAndReturnDomainObject() {
        when(mapper.toDocument(any(NotificationLog.class))).thenReturn(sampleLogDocument);
        when(springDataRepo.save(any(NotificationLogDocument.class))).thenReturn(sampleLogDocument);
        when(mapper.toDomain(any(NotificationLogDocument.class))).thenReturn(sampleLog);

        NotificationLog savedLog = notificationLogRepository.save(sampleLog);

        assertNotNull(savedLog);
        assertEquals(sampleLog, savedLog);
        verify(mapper, times(1)).toDocument(sampleLog);
        verify(springDataRepo, times(1)).save(sampleLogDocument);
        verify(mapper, times(1)).toDomain(sampleLogDocument);
    }

    @Test
    void findById_ShouldReturnNotificationLogWhenFound() {
        when(springDataRepo.findById("log-id-1")).thenReturn(Optional.of(sampleLogDocument));
        when(mapper.toDomain(any(NotificationLogDocument.class))).thenReturn(sampleLog);

        Optional<NotificationLog> foundLog = notificationLogRepository.findById("log-id-1");

        assertTrue(foundLog.isPresent());
        assertEquals(sampleLog, foundLog.get());
        verify(springDataRepo, times(1)).findById("log-id-1");
        verify(mapper, times(1)).toDomain(sampleLogDocument);
    }

    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<NotificationLog> foundLog = notificationLogRepository.findById("non-existent-id");

        assertFalse(foundLog.isPresent());
        verify(springDataRepo, times(1)).findById("non-existent-id");
        verify(mapper, never()).toDomain(any(NotificationLogDocument.class));
    }

    @Test
    void findByUserId_ShouldReturnListOfNotificationLogs() {
        List<NotificationLogDocument> logDocuments = Arrays.asList(sampleLogDocument, sampleLogDocument);
        List<NotificationLog> expectedLogs = Arrays.asList(sampleLog, sampleLog);

        when(springDataRepo.findByUserId(sampleUserId)).thenReturn(logDocuments);
        when(mapper.toDomain(any(NotificationLogDocument.class))).thenReturn(sampleLog);

        List<NotificationLog> foundLogs = notificationLogRepository.findByUserId(sampleUserId);

        assertFalse(foundLogs.isEmpty());
        assertEquals(expectedLogs.size(), foundLogs.size());
        assertEquals(expectedLogs, foundLogs);
        verify(springDataRepo, times(1)).findByUserId(sampleUserId);
        verify(mapper, times(logDocuments.size())).toDomain(any(NotificationLogDocument.class));
    }

    @Test
    void findByUserId_ShouldReturnEmptyListWhenNoneFound() {
        when(springDataRepo.findByUserId(sampleUserId)).thenReturn(Collections.emptyList());

        List<NotificationLog> foundLogs = notificationLogRepository.findByUserId(sampleUserId);

        assertTrue(foundLogs.isEmpty());
        verify(springDataRepo, times(1)).findByUserId(sampleUserId);
        verify(mapper, never()).toDomain(any(NotificationLogDocument.class));
    }

    @Test
    void findByEventId_ShouldReturnListOfNotificationLogs() {
        List<NotificationLogDocument> logDocuments = Collections.singletonList(sampleLogDocument);
        List<NotificationLog> expectedLogs = Collections.singletonList(sampleLog);

        when(springDataRepo.findByEventId(sampleEventId)).thenReturn(logDocuments);
        when(mapper.toDomain(any(NotificationLogDocument.class))).thenReturn(sampleLog);

        List<NotificationLog> foundLogs = notificationLogRepository.findByEventId(sampleEventId);

        assertFalse(foundLogs.isEmpty());
        assertEquals(expectedLogs.size(), foundLogs.size());
        assertEquals(expectedLogs, foundLogs);
        verify(springDataRepo, times(1)).findByEventId(sampleEventId);
        verify(mapper, times(logDocuments.size())).toDomain(any(NotificationLogDocument.class));
    }

    @Test
    void findByEventId_ShouldReturnEmptyListWhenNoneFound() {
        when(springDataRepo.findByEventId("non-existent-event-id")).thenReturn(Collections.emptyList());

        List<NotificationLog> foundLogs = notificationLogRepository.findByEventId("non-existent-event-id");

        assertTrue(foundLogs.isEmpty());
        verify(springDataRepo, times(1)).findByEventId("non-existent-event-id");
        verify(mapper, never()).toDomain(any(NotificationLogDocument.class));
    }

    @Test
    void findByStatus_ShouldReturnListOfNotificationLogs() {
        NotificationStatus status = NotificationStatus.SUCCESS;
        NotificationLog successLog = new NotificationLog(
                "log-id-2", sampleEventId, sampleUserId, NotificationType.VISUAL,
                Instant.now(), status
        );
        NotificationLogDocument successLogDoc = new NotificationLogDocument(
                "log-id-2", sampleEventId, sampleUserId, NotificationType.VISUAL,
                Instant.now(), status
        );

        List<NotificationLogDocument> logDocuments = Collections.singletonList(successLogDoc);
        List<NotificationLog> expectedLogs = Collections.singletonList(successLog);

        when(springDataRepo.findByStatus(status)).thenReturn(logDocuments);
        when(mapper.toDomain(successLogDoc)).thenReturn(successLog);

        List<NotificationLog> foundLogs = notificationLogRepository.findByStatus(status);

        assertFalse(foundLogs.isEmpty());
        assertEquals(expectedLogs.size(), foundLogs.size());
        assertEquals(expectedLogs, foundLogs);
        verify(springDataRepo, times(1)).findByStatus(status);
        verify(mapper, times(logDocuments.size())).toDomain(successLogDoc);
    }

    @Test
    void findByStatus_ShouldReturnEmptyListWhenNoneFound() {
        NotificationStatus status = NotificationStatus.FAILURE;
        when(springDataRepo.findByStatus(status)).thenReturn(Collections.emptyList());

        List<NotificationLog> foundLogs = notificationLogRepository.findByStatus(status);

        assertTrue(foundLogs.isEmpty());
        verify(springDataRepo, times(1)).findByStatus(status);
        verify(mapper, never()).toDomain(any(NotificationLogDocument.class));
    }

    @Test
    void deleteById_ShouldCallSpringDataDeleteById() {
        doNothing().when(springDataRepo).deleteById("log-id-1");

        notificationLogRepository.deleteById("log-id-1");

        verify(springDataRepo, times(1)).deleteById("log-id-1");
        verifyNoMoreInteractions(mapper);
    }
}