package org.example.servletsHomework.dao;

import org.example.servletsHomework.model.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapTextDao implements TextDao {

    private static final Map<Long, List<Text>> map = new HashMap<>();

    public boolean add(Long userId, Text text) {
        List<Text> textList = map.computeIfAbsent(userId, key -> new ArrayList<>());
        return textList.add(text);
    }

    public Text getTextById(Long userId, Long textId) {
        List<Text> texts = map.get(userId);
        if (texts == null) {
            return null;
        }
        return texts.stream()
                .filter(text -> text.getTextId().equals(textId))
                .findFirst()
                .orElse(null);
    }

    public List<Text> getAllTexts(Long userId) {
        return map.get(userId);
    }


    public void delete(Long userId, Long textId) {
        map.computeIfPresent(userId, (id, texts) -> texts.stream()
                .filter(text -> !text.getTextId().equals(textId))
                .toList());
    }


    public void deleteAll(Long userId) {
        map.computeIfPresent(userId, (key, texts) -> new ArrayList<>());
    }

}
