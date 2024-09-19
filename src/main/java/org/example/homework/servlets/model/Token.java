package org.example.homework.servlets.model;

public class Token {

    private final String value;
    private final Long creationTime;

    public Token(String value) {
        this.value = value;
        this.creationTime = System.currentTimeMillis();
    }

    public String getValue() {
        return value;
    }

    public Long getCreationTime() {
        return creationTime;
    }

}
