package com.project.crystalplan.infrastructure.persistence.mongo.mapper;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.infrastructure.persistence.mongo.document.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDocument toDocument(User user) {
        if (user == null) return null;
        return new UserDocument(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public User toDomain(UserDocument document) {
        if (document == null) return null;
        return new User(
                document.getId(),
                document.getName(),
                document.getEmail(),
                document.getPassword()
        );
    }
}

