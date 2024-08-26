package org.example.servletsHomework.model;

public class User {

    private long id;
    private String username;
    private String password;
    long creationTime;

    public User(String username, String password, long id, long creationTime) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.creationTime = creationTime;
    }

    public String getUsername() {
        return username;
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

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
