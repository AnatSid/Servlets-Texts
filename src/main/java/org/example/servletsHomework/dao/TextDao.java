package org.example.servletsHomework.dao;

import org.example.servletsHomework.model.Texts;

import java.util.List;


public interface TextDao {

    boolean add(Long userId, Texts text);

    Texts getTextById(Long userId, Long textId);

    List<Texts> getAllTexts(Long userId);

    void delete(Long userId, Long textId);

    void deleteAll(Long userId);

}
