package org.example.homework.servlets.dto;

public class TextCreateResponse {
    private String text;
    private String message;
    private long textId;

    public void setText(String text) {
        this.text = text;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTextId(long textId) {
        this.textId = textId;
    }

    public String getText() {
        return text;
    }

    public String getMessage() {
        return message;
    }

    public long getTextId() {
        return textId;
    }

}
