package org.example.servletsHomework.service;

import org.springframework.stereotype.Component;

@Component
public class InMemoryIdGenerator implements IdGenerator {

    private Long id = 0L;

    @Override
    public Long getNextId() {
        return ++id;
    }
}
