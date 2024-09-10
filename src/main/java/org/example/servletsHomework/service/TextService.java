package org.example.servletsHomework.service;


import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.exception.NotFoundException;
import org.example.servletsHomework.model.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TextService {

    private final TextDao textDao;

    @Autowired
    public TextService(TextDao textDao) {
        this.textDao = textDao;
    }

    public boolean addText(Long userId, Text text) {
        return textDao.add(userId, text);
    }

    public Text getTextById(Long userId, Long textId) {
        return Optional.ofNullable(textDao.getTextById(userId, textId))
                .orElseThrow(() -> new NotFoundException("Text not found with id " + textId));
    }


    public List<Text> getAllTexts(Long userId) {
        return Optional.ofNullable(textDao.getAllTexts(userId))
                .orElse(List.of());
    }

    public void deleteTextById(Long userId, Long textId) {
        textDao.delete(userId, textId);
    }

    public void deleteAll(Long userId) {
        textDao.deleteAll(userId);
    }


}