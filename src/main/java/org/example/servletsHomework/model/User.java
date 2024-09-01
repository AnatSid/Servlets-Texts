package org.example.servletsHomework.model;

public class User {

    private long id;
    private String username;
    private String password;
    private long lastLoginTime;

    public User(String username, String password, long id, long lastLoginTime) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.lastLoginTime = lastLoginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
