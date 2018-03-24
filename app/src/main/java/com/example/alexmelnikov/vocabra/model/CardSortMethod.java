package com.example.alexmelnikov.vocabra.model;

/**
 * Created by AlexMelnikov on 24.03.18.
 */

public class CardSortMethod {

    int id;
    String name;
    boolean ascending;

    public CardSortMethod(int id, String name, boolean ascending) {
        this.id = id;
        this.name = name;
        this.ascending = ascending;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
