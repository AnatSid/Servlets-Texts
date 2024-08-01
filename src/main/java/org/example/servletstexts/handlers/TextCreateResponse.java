package org.example.servletstexts.handlers;

public class TextCreateResponse {
    private String text;
    private String message;
    private int textId;


    public void setText(String text) {
        this.text = text;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTextId(int textId) {
        this.textId = textId;
    }

    public String getText() {
        return text;
    }

    public String getMessage() {
        return message;
    }

    public int getTextId() {
        return textId;
    }
}
