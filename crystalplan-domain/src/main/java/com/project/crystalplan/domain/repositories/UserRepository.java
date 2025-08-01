package com.project.crystalplan.domain.repositories;

import com.project.crystalplan.domain.models.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByIdAndActiveTrue(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndActiveTrue(String email);
    boolean existsByEmail(String email);
    boolean existsById(String id);
    void deleteById(String id);
}