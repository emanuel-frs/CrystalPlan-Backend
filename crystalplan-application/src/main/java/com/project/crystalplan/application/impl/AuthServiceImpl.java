package com.project.crystalplan.application.impl;

import com.project.crystalplan.domain.services.TokenService;
import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.domain.services.AuthService;
import com.project.crystalplan.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(String email, String rawPassword) {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }
        return tokenService.generateToken(user.getId(), user.getEmail());
    }
}
