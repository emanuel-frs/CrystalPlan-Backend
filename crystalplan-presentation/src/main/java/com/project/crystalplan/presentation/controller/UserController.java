package com.project.crystalplan.presentation.controller;

import com.project.crystalplan.application.services.UserService;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.presentation.dtos.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity
                .created(URI.create("/api/users/" + createdUser.getId()))
                .body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Optional<User>> getByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @Valid @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Optional<User>> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> user = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(user);
    }
}
