package com.project.crystalplan.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.services.UserService;
import com.project.crystalplan.presentation.dtos.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para lidar com tipos de data/hora se o User tiver
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sampleUser = new User("user-id-1", "John Doe", "john.doe@example.com", "password123");
    }

    @Test
    void shouldCreateUser() throws Exception {
        User userToCreate = new User(null, "New User", "new.user@example.com", "newpass");
        User createdUser = new User("new-user-id", "New User", "new.user@example.com", "newpass");

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/new-user-id"))
                .andExpect(jsonPath("$.id", is("new-user-id")))
                .andExpect(jsonPath("$.email", is("new.user@example.com")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void shouldReturnConflictWhenCreatingUserWithExistingEmail() throws Exception {
        User userWithExistingEmail = new User(null, "Existing User", "john.doe@example.com", "pass");

        when(userService.createUser(any(User.class)))
                .thenThrow(new InvalidArgumentException("Já existe um usuário com este e-mail."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithExistingEmail)))
                .andExpect(status().isBadRequest()); // Ou 409 Conflict, dependendo da sua ExceptionHandler

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userService.getUserById("user-id-1")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/user-id-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("user-id-1")))
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(userService, times(1)).getUserById("user-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenUserByIdDoesNotExist() throws Exception {
        when(userService.getUserById("non-existent-id")).thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/api/users/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById("non-existent-id");
    }

    @Test
    void shouldGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/email/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("user-id-1")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
    }

    @Test
    void shouldReturnNotFoundWhenUserByEmailDoesNotExist() throws Exception {
        when(userService.getUserByEmail("nonexistent@example.com"))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado com o e-mail fornecido"));

        mockMvc.perform(get("/api/users/email/nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
    }


    @Test
    void shouldUpdateUser() throws Exception {
        User updatedUserDetails = new User(
                "user-id-1", "Johnathan Doe", "john.doe.updated@example.com", "newsecurepass"
        );

        when(userService.updateUser(eq("user-id-1"), any(User.class))).thenReturn(updatedUserDetails);

        mockMvc.perform(put("/api/users/user-id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("user-id-1")))
                .andExpect(jsonPath("$.email", is("john.doe.updated@example.com")));

        verify(userService, times(1)).updateUser(eq("user-id-1"), any(User.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        User nonExistentUser = new User("non-existent-id", "Non Existent", "non@example.com", "pass");

        when(userService.updateUser(eq("non-existent-id"), any(User.class)))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        mockMvc.perform(put("/api/users/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUser)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq("non-existent-id"), any(User.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser("user-id-1");

        mockMvc.perform(delete("/api/users/user-id-1"))
                .andExpect(status().isNoContent()); // 204 No Content for successful deletion

        verify(userService, times(1)).deleteUser("user-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        doThrow(new EntityNotFoundException("Usuário não encontrado")).when(userService).deleteUser("non-existent-id");

        mockMvc.perform(delete("/api/users/non-existent-id"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser("non-existent-id");
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");

        when(userService.login("john.doe@example.com", "password123")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("user-id-1")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(userService, times(1)).login("john.doe@example.com", "password123");
    }

    @Test
    void shouldReturnNotFoundOnLoginWhenUserDoesNotExist() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "anypass");

        when(userService.login("nonexistent@example.com", "anypass"))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).login("nonexistent@example.com", "anypass");
    }

    @Test
    void shouldReturnBadRequestOnLoginWhenInvalidPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "wrongpass");

        when(userService.login("john.doe@example.com", "wrongpass"))
                .thenThrow(new InvalidArgumentException("Senha inválida."));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).login("john.doe@example.com", "wrongpass");
    }
}