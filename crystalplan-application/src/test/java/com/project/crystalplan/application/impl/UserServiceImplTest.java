package com.project.crystalplan.application.impl;

import com.project.crystalplan.domain.enums.NotificationType;
import com.project.crystalplan.domain.enums.Recurrence;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.Event;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.repositories.UserRepository;
import com.project.crystalplan.domain.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private User updatedUser;
    private User loginUser;

    @BeforeEach
    void setUp() {
        LocalDateTime pastTime = LocalDateTime.now().minusDays(5);

        sampleUser = new User();
        sampleUser.setId("user-id-1");
        sampleUser.setUuid(UUID.randomUUID().toString());
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setPassword("Password@123");
        sampleUser.setBirthday(LocalDate.of(2000, 1, 1));
        sampleUser.setCreatedAt(pastTime);
        sampleUser.setUpdatedAt(pastTime);
        sampleUser.setActive(true);

        updatedUser = new User();
        updatedUser.setName("John Doe Updated");
        updatedUser.setEmail("john.doe.updated@example.com");
        updatedUser.setPassword("NewPassword@123");
        updatedUser.setBirthday(LocalDate.of(2001, 2, 2));

        loginUser = new User();
        loginUser.setId("user-id-login");
        loginUser.setUuid(UUID.randomUUID().toString());
        loginUser.setName("Login User");
        loginUser.setEmail("login@example.com");
        loginUser.setPassword("Password@123");
        loginUser.setBirthday(LocalDate.of(1990, 1, 1));
        loginUser.setCreatedAt(pastTime.minusHours(1));
        loginUser.setUpdatedAt(pastTime.minusHours(1));
        loginUser.setActive(true);
    }

    @Test
    void createUser_ShouldSaveUserAndGenerateBirthdayEvents() {
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(eventService.createEvent(any(Event.class))).thenReturn(new Event());

        User createdUser = userService.createUser(sampleUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertNotNull(capturedUser.getUuid());
        assertNotNull(capturedUser.getCreatedAt());
        assertNotNull(capturedUser.getUpdatedAt());
        assertTrue(capturedUser.getCreatedAt().isAfter(sampleUser.getCreatedAt()) || capturedUser.getCreatedAt().isEqual(sampleUser.getCreatedAt()));
        assertTrue(capturedUser.getUpdatedAt().isAfter(sampleUser.getUpdatedAt()) || capturedUser.getUpdatedAt().isEqual(sampleUser.getUpdatedAt()));
        assertTrue(capturedUser.isActive());
        assertEquals(sampleUser.getName(), capturedUser.getName());
        assertEquals(sampleUser.getEmail(), capturedUser.getEmail());

        verify(eventService, times(20)).createEvent(any(Event.class));
        assertEquals(capturedUser.getName(), createdUser.getName());
        assertEquals(capturedUser.getEmail(), createdUser.getEmail());
    }

    @Test
    void createUser_ShouldThrowInvalidArgumentExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(true);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                userService.createUser(sampleUser));

        assertEquals("Já existe um usuário com este e-mail.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowInvalidArgumentExceptionForInvalidName() {
        sampleUser.setName("");
        assertThrows(InvalidArgumentException.class, () -> userService.createUser(sampleUser));
    }

    @Test
    void createUser_ShouldThrowInvalidArgumentExceptionForInvalidEmail() {
        sampleUser.setEmail("invalid-email");
        assertThrows(InvalidArgumentException.class, () -> userService.createUser(sampleUser));
    }

    @Test
    void createUser_ShouldThrowInvalidArgumentExceptionForInvalidPassword() {
        sampleUser.setPassword("short");
        assertThrows(InvalidArgumentException.class, () -> userService.createUser(sampleUser));
    }

    @Test
    void createUser_ShouldThrowInvalidArgumentExceptionForNullBirthday() {
        sampleUser.setBirthday(null);
        assertThrows(InvalidArgumentException.class, () -> userService.createUser(sampleUser));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findByIdAndActiveTrue(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserById(sampleUser.getId());

        assertTrue(result.isPresent());
        assertEquals(sampleUser, result.get());
        verify(userRepository).findByIdAndActiveTrue(sampleUser.getId());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentUserById() {
        when(userRepository.findByIdAndActiveTrue("non-existent-id")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById("non-existent-id"));

        assertEquals("Usuário não encontrado ou inativo", exception.getMessage());
        verify(userRepository).findByIdAndActiveTrue("non-existent-id");
    }

    @Test
    void shouldGetUserByEmailSuccessfully() {
        when(userRepository.findByEmailAndActiveTrue(sampleUser.getEmail())).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserByEmail(sampleUser.getEmail());

        assertTrue(result.isPresent());
        assertEquals(sampleUser, result.get());
        verify(userRepository).findByEmailAndActiveTrue(sampleUser.getEmail());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentUserByEmail() {
        when(userRepository.findByEmailAndActiveTrue("nonexistent@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByEmail("nonexistent@example.com"));

        assertEquals("Usuário não encontrado com o e-mail fornecido ou inativo", exception.getMessage());
        verify(userRepository).findByEmailAndActiveTrue("nonexistent@example.com");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User existingUserForUpdate = new User();
        existingUserForUpdate.setId(sampleUser.getId());
        existingUserForUpdate.setUuid(sampleUser.getUuid());
        existingUserForUpdate.setName(sampleUser.getName());
        existingUserForUpdate.setEmail(sampleUser.getEmail());
        existingUserForUpdate.setPassword(sampleUser.getPassword());
        existingUserForUpdate.setBirthday(sampleUser.getBirthday());
        existingUserForUpdate.setCreatedAt(sampleUser.getCreatedAt());
        existingUserForUpdate.setUpdatedAt(sampleUser.getUpdatedAt());
        existingUserForUpdate.setActive(true);

        when(userRepository.findByIdAndActiveTrue(sampleUser.getId())).thenReturn(Optional.of(existingUserForUpdate));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(sampleUser.getId(), updatedUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(sampleUser.getId(), capturedUser.getId());
        assertEquals(updatedUser.getName(), capturedUser.getName());
        assertEquals(updatedUser.getEmail(), capturedUser.getEmail());
        assertEquals(updatedUser.getPassword(), capturedUser.getPassword());
        assertEquals(updatedUser.getBirthday(), capturedUser.getBirthday());
        assertNotNull(capturedUser.getUpdatedAt());
        assertTrue(capturedUser.getUpdatedAt().isAfter(existingUserForUpdate.getUpdatedAt()) || capturedUser.getUpdatedAt().isEqual(existingUserForUpdate.getUpdatedAt()), "updatedAt deve ser posterior ou igual ao original");
        assertTrue(capturedUser.isActive());

        assertEquals(capturedUser, result);
        verify(userRepository).findByIdAndActiveTrue(sampleUser.getId());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.findByIdAndActiveTrue("non-existent-id")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser("non-existent-id", updatedUser));

        assertEquals("Usuário não encontrado ou inativo", exception.getMessage());
        verify(userRepository).findByIdAndActiveTrue("non-existent-id");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        User existingUserForDelete = new User();
        existingUserForDelete.setId(sampleUser.getId());
        existingUserForDelete.setUuid(sampleUser.getUuid());
        existingUserForDelete.setName(sampleUser.getName());
        existingUserForDelete.setEmail(sampleUser.getEmail());
        existingUserForDelete.setPassword(sampleUser.getPassword());
        existingUserForDelete.setBirthday(sampleUser.getBirthday());
        existingUserForDelete.setCreatedAt(sampleUser.getCreatedAt());
        existingUserForDelete.setUpdatedAt(sampleUser.getUpdatedAt());
        existingUserForDelete.setActive(true);

        when(userRepository.findByIdAndActiveTrue(sampleUser.getId())).thenReturn(Optional.of(existingUserForDelete));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deleteUser(sampleUser.getId());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertFalse(capturedUser.isActive());
        assertNotNull(capturedUser.getUpdatedAt());
        assertTrue(capturedUser.getUpdatedAt().isAfter(existingUserForDelete.getUpdatedAt()) || capturedUser.getUpdatedAt().isEqual(existingUserForDelete.getUpdatedAt()), "updatedAt deve ser posterior ou igual ao original");

        verify(userRepository).findByIdAndActiveTrue(sampleUser.getId());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentUser() {
        when(userRepository.findByIdAndActiveTrue("non-existent-id")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUser("non-existent-id"));

        assertEquals("Usuário não encontrado ou já inativo", exception.getMessage());
        verify(userRepository).findByIdAndActiveTrue("non-existent-id");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmailAndActiveTrue(loginUser.getEmail())).thenReturn(Optional.of(loginUser));

        Optional<User> result = userService.login(loginUser.getEmail(), loginUser.getPassword());

        assertTrue(result.isPresent());
        assertEquals(loginUser, result.get());
        verify(userRepository).findByEmailAndActiveTrue(loginUser.getEmail());
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenPasswordDoesNotMatchOnLogin() {
        when(userRepository.findByEmailAndActiveTrue(loginUser.getEmail())).thenReturn(Optional.of(loginUser));

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                userService.login(loginUser.getEmail(), "wrongPassword"));

        assertEquals("Senha inválida.", exception.getMessage());
        verify(userRepository).findByEmailAndActiveTrue(loginUser.getEmail());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFoundOnLogin() {
        when(userRepository.findByEmailAndActiveTrue("nonexistent@example.com")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.login("nonexistent@example.com", "anypassword"));

        assertEquals("Usuário não encontrado ou inativo", exception.getMessage());
        verify(userRepository).findByEmailAndActiveTrue("nonexistent@example.com");
    }
}