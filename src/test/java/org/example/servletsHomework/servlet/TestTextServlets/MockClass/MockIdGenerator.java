package org.example.servletsHomework.servlet.TestTextServlets.MockClass;

import org.example.servletsHomework.model.IdGenerator;

public class MockIdGenerator implements IdGenerator {

    private final Long id;

    public MockIdGenerator(Long id) {
        this.id = id;
    }

    @Override
    public Long getNextId() {
        return id;
    }
}
