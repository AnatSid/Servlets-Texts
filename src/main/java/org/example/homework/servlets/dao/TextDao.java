package org.example.homework.servlets.dao;

import org.example.homework.servlets.model.Text;

import java.util.List;


public interface TextDao {

    boolean add(Long userId, Text text);

    Text getTextById(Long userId, Long textId);

    List<Text> getAllTexts(Long userId);

    void delete(Long userId, Long textId);

    void deleteAll(Long userId);

}
