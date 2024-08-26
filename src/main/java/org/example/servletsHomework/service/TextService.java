package org.example.servletsHomework.service;


import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.model.Texts;

import java.util.List;

public class TextService {

    private final TextDao textDao;

    public TextService(TextDao textDao) {
        this.textDao = textDao;
    }

    public boolean addText(Long userId, Texts text) {
        return textDao.add(userId, text);
    }

    public Texts getTextById(Long userId, Long textId) {
        return textDao.getTextById(userId, textId);
    }

    public List<Texts> getAllTexts(Long userId) {
        return textDao.getAllTexts(userId);
    }

    public void deleteTextById(Long userId, Long textId) {
        textDao.delete(userId, textId);
    }

    public void deleteAll(Long userId) {
        textDao.deleteAll(userId);
    }


}