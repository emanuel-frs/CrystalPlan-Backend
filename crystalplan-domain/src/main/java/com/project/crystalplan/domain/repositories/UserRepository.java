package com.project.crystalplan.domain.repositories;

import com.project.crystalplan.domain.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(String id);
    boolean existsById(String id);
}
