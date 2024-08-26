package org.example.servletsHomework.servlet.TestTextServlets.MockClass;

import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.model.Texts;

import java.util.ArrayList;
import java.util.List;

public class MockTextDao2 implements TextDao {

    public List<String> messages = new ArrayList<>();


    public boolean add(Long userId, Texts text) {
        return true;
    }

    public Texts getTextById(Long userId, Long textId) {
        if(textId>10){
           return null;
        }
        return new Texts(userId,"TestText");
    }

    public List<Texts> getAllTexts(Long userId) {
        return List.of(new Texts(userId,"Test"));
    }


    public void delete(Long userId, Long textId) {
        messages.add("deleteSuccess");
    }


    public void deleteAll(Long userId) {
        messages.add("deleteAllSuccess");
    }
}
