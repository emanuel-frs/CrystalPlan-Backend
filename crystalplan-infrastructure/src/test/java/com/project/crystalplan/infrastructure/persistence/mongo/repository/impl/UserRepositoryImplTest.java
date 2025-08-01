package com.project.crystalplan.infrastructure.persistence.mongo.repository.impl;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.infrastructure.persistence.mongo.document.UserDocument;
import com.project.crystalplan.infrastructure.persistence.mongo.mapper.UserMapper;
import com.project.crystalplan.infrastructure.persistence.mongo.repository.springdata.SpringDataUserMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private SpringDataUserMongoRepository springDataRepo;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User sampleUser;
    private UserDocument sampleUserDocument;
    private UserDocument sampleInactiveUserDocument;

    @BeforeEach
    void setUp() {
        LocalDate birthday = LocalDate.of(2000, 1, 1);
        LocalDateTime now = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();

        sampleUser = new User("user-id-1", "John Doe", "john.doe@example.com", "password123", birthday);
        sampleUser.setUuid(uuid);
        sampleUser.setCreatedAt(now.minusDays(5));
        sampleUser.setUpdatedAt(now.minusDays(1));
        sampleUser.setActive(true);

        sampleUserDocument = new UserDocument();
        sampleUserDocument.setId("user-id-1");
        sampleUserDocument.setUuid(uuid);
        sampleUserDocument.setName("John Doe");
        sampleUserDocument.setEmail("john.doe@example.com");
        sampleUserDocument.setPassword("password123");
        sampleUserDocument.setBirthday(birthday);
        sampleUserDocument.setCreatedAt(now.minusDays(5));
        sampleUserDocument.setUpdatedAt(now.minusDays(1));
        sampleUserDocument.setActive(true);

        sampleInactiveUserDocument = new UserDocument();
        sampleInactiveUserDocument.setId("user-id-2");
        sampleInactiveUserDocument.setUuid(UUID.randomUUID().toString());
        sampleInactiveUserDocument.setName("Jane Doe");
        sampleInactiveUserDocument.setEmail("jane.doe@example.com");
        sampleInactiveUserDocument.setPassword("password456");
        sampleInactiveUserDocument.setBirthday(LocalDate.of(1995, 5, 5));
        sampleInactiveUserDocument.setCreatedAt(now.minusDays(10));
        sampleInactiveUserDocument.setUpdatedAt(now.minusDays(2));
        sampleInactiveUserDocument.setActive(false);
    }

    @Test
    void save_ShouldSaveUserAndReturnDomainObject() {
        when(mapper.toDocument(any(User.class))).thenReturn(sampleUserDocument);
        when(springDataRepo.save(any(UserDocument.class))).thenReturn(sampleUserDocument);
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);

        User savedUser = userRepository.save(sampleUser);

        verify(mapper).toDocument(sampleUser);
        verify(springDataRepo).save(sampleUserDocument);
        verify(mapper).toDomain(sampleUserDocument);
        assertEquals(sampleUser, savedUser);
    }

    @Test
    void findById_ShouldReturnUserWhenFound() {
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.findById("user-id-1")).thenReturn(Optional.of(sampleUserDocument));

        Optional<User> foundUser = userRepository.findById("user-id-1");

        verify(springDataRepo).findById("user-id-1");
        verify(mapper).toDomain(sampleUserDocument);
        assertTrue(foundUser.isPresent());
        assertEquals(sampleUser, foundUser.get());
    }

    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findById("non-existent-id");

        verify(springDataRepo).findById("non-existent-id");
        verify(mapper, never()).toDomain(any(UserDocument.class));
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByIdAndActiveTrue_ShouldReturnUserWhenFoundAndActive() {
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.findByIdAndActiveTrue("user-id-1")).thenReturn(Optional.of(sampleUserDocument));

        Optional<User> foundUser = userRepository.findByIdAndActiveTrue("user-id-1");

        verify(springDataRepo).findByIdAndActiveTrue("user-id-1");
        verify(mapper).toDomain(sampleUserDocument);
        assertTrue(foundUser.isPresent());
        assertEquals(sampleUser, foundUser.get());
    }

    @Test
    void findByIdAndActiveTrue_ShouldReturnEmptyOptionalWhenFoundButInactive() {
        when(springDataRepo.findByIdAndActiveTrue("user-id-2")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByIdAndActiveTrue("user-id-2");

        verify(springDataRepo).findByIdAndActiveTrue("user-id-2");
        verify(mapper, never()).toDomain(any(UserDocument.class));
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByIdAndActiveTrue_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findByIdAndActiveTrue("non-existent-id")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByIdAndActiveTrue("non-existent-id");

        verify(springDataRepo).findByIdAndActiveTrue("non-existent-id");
        verify(mapper, never()).toDomain(any(UserDocument.class));
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_ShouldReturnUserWhenFound() {
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUserDocument));

        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        verify(springDataRepo).findByEmail("john.doe@example.com");
        verify(mapper).toDomain(sampleUserDocument);
        assertTrue(foundUser.isPresent());
        assertEquals(sampleUser, foundUser.get());
    }

    @Test
    void findByEmail_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        verify(springDataRepo).findByEmail("nonexistent@example.com");
        verify(mapper, never()).toDomain(any(UserDocument.class));
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmailAndActiveTrue_ShouldReturnUserWhenFoundAndActive() {
        when(mapper.toDomain(any(UserDocument.class))).thenReturn(sampleUser);
        when(springDataRepo.findByEmailAndActiveTrue("john.doe@example.com")).thenReturn(Optional.of(sampleUserDocument));

        Optional<User> foundUser = userRepository.findByEmailAndActiveTrue("john.doe@example.com");

        verify(springDataRepo).findByEmailAndActiveTrue("john.doe@example.com");
        verify(mapper).toDomain(sampleUserDocument);
        assertTrue(foundUser.isPresent());
        assertEquals(sampleUser, foundUser.get());
    }

    @Test
    void findByEmailAndActiveTrue_ShouldReturnEmptyOptionalWhenFoundButInactive() {
        when(springDataRepo.findByEmailAndActiveTrue("jane.doe@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmailAndActiveTrue("jane.doe@example.com");

        verify(springDataRepo).findByEmailAndActiveTrue("jane.doe@example.com");
        verify(mapper, never()).toDomain(any(UserDocument.class));
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmailAndActiveTrue_ShouldReturnEmptyOptionalWhenNotFound() {
        when(springDataRepo.findByEmailAndActiveTrue("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmailAndActiveTrue("nonexistent@example.com");

        verify(springDataRepo).findByEmailAndActiveTrue("nonexistent@example.com");
        verify(mapper, never()).toDomain(any(UserDocument.class));
        assertFalse(foundUser.isPresent());
    }

    @Test
    void existsByEmail_ShouldReturnTrueWhenExists() {
        when(springDataRepo.existsByEmail("john.doe@example.com")).thenReturn(true);

        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        verify(springDataRepo).existsByEmail("john.doe@example.com");
        assertTrue(exists);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenNotExists() {
        when(springDataRepo.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        verify(springDataRepo).existsByEmail("nonexistent@example.com");
        assertFalse(exists);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void deleteById_ShouldCallSpringDataDeleteById() {
        userRepository.deleteById("user-id-1");

        verify(springDataRepo).deleteById("user-id-1");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void existsById_ShouldReturnTrueWhenExists() {
        when(springDataRepo.existsById("user-id-1")).thenReturn(true);

        boolean exists = userRepository.existsById("user-id-1");

        verify(springDataRepo).existsById("user-id-1");
        assertTrue(exists);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void existsById_ShouldReturnFalseWhenNotExists() {
        when(springDataRepo.existsById("non-existent-id")).thenReturn(false);

        boolean exists = userRepository.existsById("non-existent-id");

        verify(springDataRepo).existsById("non-existent-id");
        assertFalse(exists);
        verifyNoMoreInteractions(mapper);
    }
}