package org.example.servletsHomework.model;

public class RealIdGenerator implements IdGenerator{

    private Long id = 0L;

    @Override
    public Long getNextId() {
        return ++id;
    }
}
