package org.example.homework.servlets.servlet.stubTests.stubClass;

import org.example.homework.servlets.service.IdGenerator;

public class StubIdGenerator implements IdGenerator {

    private final Long id;

    public StubIdGenerator(Long id) {
        this.id = id;
    }

    @Override
    public Long getNextId() {
        return id;
    }
}
