package com.project.crystalplan.application.impl;

import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("1", "John Doe", "john.doe@example.com", "password123");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User createdUser = userService.createUser(sampleUser);

        assertThat(createdUser).isEqualTo(sampleUser);
        verify(userRepository, times(1)).existsByEmail(sampleUser.getEmail());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void shouldThrowInvalidArgumentExceptionWhenCreatingUserWithExistingEmail() {
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(true);

        assertThrows(InvalidArgumentException.class, () -> userService.createUser(sampleUser));
        verify(userRepository, times(1)).existsByEmail(sampleUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserById("1");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sampleUser);
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentUserById() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById("99"));
        verify(userRepository, times(1)).findById("99");
    }

    @Test
    void shouldGetUserByEmailSuccessfully() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sampleUser);
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentUserByEmail() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail("nonexistent@example.com"));
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User updatedUserDetails = new User("1", "Jane Doe", "jane.doe@example.com", "newpassword");
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUserDetails);

        User result = userService.updateUser("1", updatedUserDetails);

        assertThat(result).isEqualTo(updatedUserDetails);
        // Verify that existing user fields were updated before saving
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).save(argThat(user ->
                user.getId().equals("1") &&
                        user.getName().equals("Jane Doe") &&
                        user.getEmail().equals("jane.doe@example.com") &&
                        user.getPassword().equals("newpassword")
        ));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentUser() {
        User nonExistentUser = new User("99", "Non Existent", "non@example.com", "pass");
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser("99", nonExistentUser));
        verify(userRepository, times(1)).findById("99");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        when(userRepository.existsById("1")).thenReturn(true);
        doNothing().when(userRepository).deleteById("1");

        userService.deleteUser("1");

        verify(userRepository, times(1)).existsById("1");
        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById("99")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser("99"));

        verify(userRepository, times(1)).existsById("99");
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));

        Optional<User> loggedInUser = userService.login("john.doe@example.com", "password123");

        assertThat(loggedInUser).isPresent();
        assertThat(loggedInUser.get()).isEqualTo(sampleUser);
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void shouldThrowEntityNotFoundExceptionOnLoginWhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.login("nonexistent@example.com", "anypass"));
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void shouldThrowInvalidArgumentExceptionOnLoginWithIncorrectPassword() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));

        assertThrows(InvalidArgumentException.class, () -> userService.login("john.doe@example.com", "wrongpassword"));
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }
}