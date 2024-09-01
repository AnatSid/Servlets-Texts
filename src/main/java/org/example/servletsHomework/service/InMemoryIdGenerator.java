package org.example.servletsHomework.service;

public class InMemoryIdGenerator implements IdGenerator {

    private Long id = 0L;

    @Override
    public Long getNextId() {
        return ++id;
    }
}
