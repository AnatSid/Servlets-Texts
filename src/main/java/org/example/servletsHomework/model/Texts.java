package org.example.servletsHomework.model;



public class Texts {

    private Long id;
    private String value;


    public Texts(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Texts{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

}
