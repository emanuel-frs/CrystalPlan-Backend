package com.project.crystalplan.application.services.impl;

import com.project.crystalplan.application.services.UserService;
import com.project.crystalplan.domain.exceptions.EntityNotFoundException;
import com.project.crystalplan.domain.exceptions.InvalidArgumentException;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new InvalidArgumentException("Já existe um usuário com este e-mail.");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o e-mail fornecido")));
    }

    @Override
    public User updateUser(String id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if (!user.getPassword().equals(password)) {
            throw new InvalidArgumentException("Senha inválida.");
        }
        return Optional.of(user);
    }
}
