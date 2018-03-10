package com.example.alexmelnikov.vocabra.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public class Card extends RealmObject {

    @PrimaryKey
    private int id;

    private String front;

    private String back;

    private Language frontLanguage;

    private Language backLanguage;

    private Deck deck;

    private boolean isReadyForTraining;

    private Date lastTimeTrained;

    private int timesTrained;

    public Card() {}

    public Card(int id, String front, String back, Language frontLanguage,
                    Language backLanguage, Deck deck) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.frontLanguage = frontLanguage;
        this.backLanguage = backLanguage;
        this.deck = deck;
        this.isReadyForTraining = true;
        this.timesTrained = 0;
    }

    public int getId() {
        return id;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public Language getFrontLanguage() {
        return frontLanguage;
    }

    public void setFrontLanguage(Language frontLanguage) {
        this.frontLanguage = frontLanguage;
    }

    public Language getBackLanguage() {
        return backLanguage;
    }

    public void setBackLanguage(Language backLanguage) {
        this.backLanguage = backLanguage;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public boolean isReadyForTraining() {
        return isReadyForTraining;
    }

    public void setReadyForTraining(boolean readyForTraining) {
        isReadyForTraining = readyForTraining;
    }

    public Date getLastTimeTrained() {
        return lastTimeTrained;
    }

    public void setLastTimeTrained(Date lastTimeTrained) {
        this.lastTimeTrained = lastTimeTrained;
    }

    public int getTimesTrained() {
        return timesTrained;
    }

    public void setTimesTrained(int timesTrained) {
        this.timesTrained = timesTrained;
    }
}
