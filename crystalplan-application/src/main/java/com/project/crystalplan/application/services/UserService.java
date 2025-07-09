package com.project.crystalplan.application.services;

import com.project.crystalplan.domain.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(String id);
    Optional<User> getUserByEmail(String email);
    User updateUser(String id, User user);
    void deleteUser(String id);
    Optional<User> login(String email, String password);
}
