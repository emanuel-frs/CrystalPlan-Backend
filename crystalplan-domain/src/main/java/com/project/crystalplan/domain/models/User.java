package com.project.crystalplan.domain.models;

import java.time.LocalDate;

public class User extends BaseModel{
    private String id;
    private String name;
    private String email;
    private String password;
    private LocalDate birthday;

    public User() {}

    public User(String id, String name, String email, String password, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getBirthday() { return  birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
}
