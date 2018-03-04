package com.example.alexmelnikov.vocabra.model;


public class SelectedLanguages {

    private int from;

    private int to;

    public SelectedLanguages(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }
}
