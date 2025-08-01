package com.project.crystalplan.domain.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldCreateUserWithAllFields() {
        String id = "user-001";
        String name = "João Silva";
        String email = "joao@example.com";
        String password = "S3nh@Fort3";
        LocalDate birthday = LocalDate.of(1990, 5, 20);

        User user = new User(id, name, email, password, birthday);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getBirthday()).isEqualTo(birthday);
    }

    @Test
    void shouldSettersAndGettersWorkCorrectly() {
        User user = new User();

        user.setId("user-002");
        user.setName("Maria Souza");
        user.setEmail("maria@example.com");
        user.setPassword("Senha@123");
        LocalDate birthday = LocalDate.of(1985, 12, 15);
        user.setBirthday(birthday);

        assertThat(user.getId()).isEqualTo("user-002");
        assertThat(user.getName()).isEqualTo("Maria Souza");
        assertThat(user.getEmail()).isEqualTo("maria@example.com");
        assertThat(user.getPassword()).isEqualTo("Senha@123");
        assertThat(user.getBirthday()).isEqualTo(birthday);
    }

    @Test
    void shouldInitializeBaseFields() {
        User user = new User();
        assertThat(user.getUuid()).isNotNull();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
    }
}
