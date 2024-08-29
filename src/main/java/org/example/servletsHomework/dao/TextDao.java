package org.example.servletsHomework.dao;

import org.example.servletsHomework.model.Text;

import java.util.List;


public interface TextDao {

    boolean add(Long userId, Text text);

    Text getTextById(Long userId, Long textId);

    List<Text> getAllTexts(Long userId);

    void delete(Long userId, Long textId);

    void deleteAll(Long userId);

}
