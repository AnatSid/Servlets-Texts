package org.example.homework.servlets.service;

import org.springframework.stereotype.Component;

@Component
public class InMemoryIdGenerator implements IdGenerator {

    private Long id = 0L;

    @Override
    public Long getNextId() {
        return ++id;
    }
}
