package org.example.servletsHomework.servlet.stubTests.stubClass;

import org.example.servletsHomework.service.IdGenerator;

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
