package com.project.crystalplan.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.exceptions.InvalidCredentialsException; // Import this!
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.services.UserService;
import com.project.crystalplan.infrastructure.security.jwt.JwtTokenProvider;
// import com.project.crystalplan.infrastructure.security.jwt.JwtAuthenticationFilter; // Likely not needed for exclusion if @WebMvcTest works as intended
import com.project.crystalplan.presentation.dtos.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                // Only exclude if it's causing issues. WebMvcTest typically only loads the Controller slice.
                // classes = {JwtAuthenticationFilter.class}
                classes = {} // Keep it empty for now unless you specifically need to exclude something that WebMvcTest is picking up
        )
)
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sampleUser = new User();
        sampleUser.setId("user-id-1");
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setPassword("password123"); // Password must match what the service expects
        sampleUser.setBirthday(LocalDate.of(1990, 5, 15));
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());
        sampleUser.setActive(true);
    }

    @Test
    void shouldCreateUser() throws Exception {
        User userToCreate = new User();
        userToCreate.setName("New User");
        userToCreate.setEmail("new.user@example.com");
        userToCreate.setPassword("newpass");
        userToCreate.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = new User();
        createdUser.setId("new-user-id");
        createdUser.setName("New User");
        createdUser.setEmail("new.user@example.com");
        createdUser.setPassword("newpass");
        createdUser.setBirthday(LocalDate.of(2000, 1, 1));
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setUpdatedAt(LocalDateTime.now());
        createdUser.setActive(true);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/new-user-id"))
                .andExpect(jsonPath("$.id", is("new-user-id")))
                .andExpect(jsonPath("$.email", is("new.user@example.com")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingUserWithExistingEmail() throws Exception {
        User userWithExistingEmail = new User();
        userWithExistingEmail.setName("Existing User");
        userWithExistingEmail.setEmail("john.doe@example.com");
        userWithExistingEmail.setPassword("pass");
        userWithExistingEmail.setBirthday(LocalDate.of(1985, 3, 20));

        when(userService.createUser(any(User.class)))
                .thenThrow(new InvalidArgumentException("Já existe um usuário com este e-mail."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithExistingEmail))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

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
        when(userService.getUserById("non-existent-id"))
                .thenReturn(Optional.empty());

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
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/email/nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User updatedUserDetails = new User();
        updatedUserDetails.setId("user-id-1");
        updatedUserDetails.setName("Johnathan Doe");
        updatedUserDetails.setEmail("john.doe.updated@example.com");
        updatedUserDetails.setPassword("newsecurepass");
        updatedUserDetails.setBirthday(LocalDate.of(1990, 5, 15));
        updatedUserDetails.setCreatedAt(sampleUser.getCreatedAt()); // Keep original created at
        updatedUserDetails.setUpdatedAt(LocalDateTime.now()); // Set updated at to now (mocked)
        updatedUserDetails.setActive(true);

        when(userService.updateUser(eq("user-id-1"), any(User.class))).thenReturn(updatedUserDetails);

        mockMvc.perform(put("/api/users/user-id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDetails))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("user-id-1")))
                .andExpect(jsonPath("$.email", is("john.doe.updated@example.com")));

        verify(userService, times(1)).updateUser(eq("user-id-1"), any(User.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        User nonExistentUser = new User();
        nonExistentUser.setId("non-existent-id");
        nonExistentUser.setName("Non Existent");
        nonExistentUser.setEmail("non@example.com");
        nonExistentUser.setPassword("pass");
        nonExistentUser.setBirthday(LocalDate.of(1999, 1, 1));

        when(userService.updateUser(eq("non-existent-id"), any(User.class)))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado para atualização ou inativo.")); // Match service message

        mockMvc.perform(put("/api/users/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUser))
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq("non-existent-id"), any(User.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser("user-id-1");

        mockMvc.perform(delete("/api/users/user-id-1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser("user-id-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        doThrow(new EntityNotFoundException("Usuário não encontrado para exclusão ou já inativo.")) // Match service message
                .when(userService).deleteUser("non-existent-id");

        mockMvc.perform(delete("/api/users/non-existent-id")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser("non-existent-id");
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");

        when(userService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(Optional.of(sampleUser));

        when(jwtTokenProvider.createToken(sampleUser.getId(), sampleUser.getEmail())).thenReturn("mocked_jwt_token");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token", is("mocked_jwt_token")))
                .andExpect(jsonPath("$.user.id", is("user-id-1")))
                .andExpect(jsonPath("$.user.email", is("john.doe@example.com")));

        verify(userService, times(1)).login(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtTokenProvider, times(1)).createToken(sampleUser.getId(), sampleUser.getEmail());
    }

    @Test
    void shouldReturnNotFoundOnLoginWhenUserDoesNotExist() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "anypass");

        when(userService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).login(loginRequest.getEmail(), loginRequest.getPassword());
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void shouldReturnUnauthorizedOnLoginWhenInvalidPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "wrongpass");

        // CORRECTED MOCK: The service throws InvalidCredentialsException for wrong password
        when(userService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenThrow(new InvalidCredentialsException("Senha inválida."));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // Now this assertion will pass

        verify(userService, times(1)).login(loginRequest.getEmail(), loginRequest.getPassword());
        verifyNoInteractions(jwtTokenProvider);
    }
}