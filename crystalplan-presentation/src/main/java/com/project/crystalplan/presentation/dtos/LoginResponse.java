package com.project.crystalplan.presentation.dtos;

import com.project.crystalplan.domain.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
}
