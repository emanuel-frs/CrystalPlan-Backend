package com.project.crystalplan.application.services.impl;

import com.project.crystalplan.application.services.UserService;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Já existe um usuário com este e-mail.");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(String id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

}

