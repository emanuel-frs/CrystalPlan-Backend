package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.services.UserService;
import com.project.crystalplan.infrastructure.security.jwt.JwtTokenProvider;
import com.project.crystalplan.presentation.dtos.LoginRequest;
import com.project.crystalplan.presentation.dtos.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Importe HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional; // Importe Optional

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity
                .created(URI.create("/api/users/" + createdUser.getId()))
                .body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        // Se o Optional estiver vazio, retorna 404 Not Found.
        // Caso contrário, retorna 200 OK com o usuário.
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        // Se o Optional estiver vazio, retorna 404 Not Found.
        // Caso contrário, retorna 200 OK com o usuário.
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @Valid @RequestBody User user) {
        // O método updateUser no serviço já lança EntityNotFoundException,
        // que será capturada pelo GlobalExceptionHandler e mapeada para 404.
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        // O método deleteUser no serviço já lança EntityNotFoundException,
        // que será capturada pelo GlobalExceptionHandler e mapeada para 404.
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // O método login no serviço retorna Optional.empty() se o usuário não for encontrado,
        // ou lança InvalidCredentialsException se a senha estiver incorreta.
        // O GlobalExceptionHandler agora lida com InvalidCredentialsException (401).
        Optional<User> userOptional = userService.login(request.getEmail(), request.getPassword());

        if (userOptional.isEmpty()) {
            // Se o usuário não foi encontrado pelo serviço (Optional vazio),
            // retorna 404 Not Found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();
        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new LoginResponse(token, user));
    }
}