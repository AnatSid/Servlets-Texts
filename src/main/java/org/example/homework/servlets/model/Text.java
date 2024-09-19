package org.example.homework.servlets.model;


import java.util.Objects;

public class Text {

    private Long textId;
    private String value;
    private Long userId;


    public Text(Long textId, String value, Long userId) {
        this.textId = textId;
        this.value = value;
        this.userId = userId;
    }

    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Text{" +
                "textId=" + textId +
                ", value='" + value + '\'' +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textId, value, userId);
    }
}
