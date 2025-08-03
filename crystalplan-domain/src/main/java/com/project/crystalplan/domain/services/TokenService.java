package com.project.crystalplan.domain.services;

public interface TokenService {
    String generateToken(String userId, String email);
    boolean validateToken(String token);
    String extractUserId(String token);
}
