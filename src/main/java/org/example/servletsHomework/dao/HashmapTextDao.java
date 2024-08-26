package org.example.servletsHomework.dao;

import org.example.servletsHomework.model.Texts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashmapTextDao implements TextDao {

    private static final Map<Long, List<Texts>> map = new HashMap<>();

    public boolean add(Long userId, Texts text) {
        List<Texts> textsList = map.computeIfAbsent(userId, (key) -> new ArrayList<>());
        return textsList.add(text);
    }

    public Texts getTextById(Long userId, Long textId) {
        List<Texts> texts = map.computeIfAbsent(userId, (key) -> new ArrayList<>());
        return texts.stream()
                .filter(texts1 -> texts1.getId().equals(textId))
                .findFirst()
                .orElse(null);
    }

    public List<Texts> getAllTexts(Long userId) {
        return map.computeIfAbsent(userId, (key) -> new ArrayList<>());
    }


    public void delete(Long userId, Long textId) {
        map.computeIfPresent(userId, (id, texts) -> texts.stream()
                .filter(text -> !text.getId().equals(textId))
                .toList());
    }


    public void deleteAll(Long userId) {
        map.computeIfPresent(userId, (key, texts) -> new ArrayList<>());
    }

}
