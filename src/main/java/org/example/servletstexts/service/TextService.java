package org.example.servletstexts.service;


import org.example.servletstexts.dao.TextsDAO;


public class TextService {

    private final TextsDAO textsDAO;

    public TextService(TextsDAO textsDAO) {
        this.textsDAO = textsDAO;
    }

    public int addText(String text) {
        return textsDAO.add(text);
    }

    public String getTextById(int id) {
        return textsDAO.get(id);
    }

    public String getAll() {
        return textsDAO.getAll().toString();
    }

    public void deleteTextById(int id) {
        textsDAO.delete(id);
    }

    public void deleteAll() {
        textsDAO.deleteAll();
    }


}