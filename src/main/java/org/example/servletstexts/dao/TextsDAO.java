package org.example.servletstexts.dao;

import java.util.HashMap;
import java.util.Map;

public class TextsDAO {

    private int currentId = 0;
    private final Map<Integer, String> texts = new HashMap<>();


    public String get(int id) {
        return texts.get(id);
    }

    public Map<Integer, String> getAll() {
        return texts;
    }

    public int add(String text) {
        currentId++;
        texts.put(currentId, text);
        return currentId;
    }

    public void delete(int id) {
        texts.remove(id);
    }

    public void deleteAll() {
        texts.clear();
        currentId = 1;
    }

}
