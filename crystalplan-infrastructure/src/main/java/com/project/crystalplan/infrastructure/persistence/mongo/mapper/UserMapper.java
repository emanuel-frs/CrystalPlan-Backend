package com.project.crystalplan.infrastructure.persistence.mongo.mapper;

import com.project.crystalplan.domain.models.User;
import com.project.crystalplan.infrastructure.persistence.mongo.document.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDocument toDocument(User user) {
        if (user == null) {
            return null;
        }
        UserDocument document = new UserDocument();
        document.setId(user.getId());
        document.setUuid(user.getUuid());
        document.setName(user.getName());
        document.setEmail(user.getEmail());
        document.setPassword(user.getPassword());
        document.setBirthday(user.getBirthday());
        document.setCreatedAt(user.getCreatedAt());
        document.setUpdatedAt(user.getUpdatedAt());
        document.setActive(user.isActive());
        return document;
    }

    public User toDomain(UserDocument document) {
        if (document == null) {
            return null;
        }
        User user = new User(
                document.getId(),
                document.getName(),
                document.getEmail(),
                document.getPassword(),
                document.getBirthday()
        );
        user.setUuid(document.getUuid());
        user.setCreatedAt(document.getCreatedAt());
        user.setUpdatedAt(document.getUpdatedAt());
        user.setActive(document.isActive());
        return user;
    }
}