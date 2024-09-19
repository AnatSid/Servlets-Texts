package org.example.homework.servlets.model;

import java.util.Objects;

public class User {

    private final long id;
    private final String username;
    private final String password;

    public User(String username, String password, long id) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }
}
