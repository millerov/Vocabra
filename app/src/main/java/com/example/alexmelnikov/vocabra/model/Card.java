package com.example.alexmelnikov.vocabra.model;

import android.support.annotation.Nullable;

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

    private String cardContext;

    private String translationDirection;

    private Language frontLanguage;

    private Language backLanguage;

    private Deck deck;

    private boolean isReadyForTraining;

    private Date lastTimeTrained;

    private int timesTrained;

    public Card() {}

    //=======================
    //ADD DECK TO CONSTRUCTOR
    //=======================
    public Card(int id, String front, String back, Language frontLanguage,
                    Language backLanguage, Deck deck, String cardContext) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.frontLanguage = frontLanguage;
        this.backLanguage = backLanguage;
        this.deck = deck;
        this.cardContext = cardContext;
        this.isReadyForTraining = true;
        this.timesTrained = 0;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCardContext() {
        return cardContext;
    }

    public void setCardContext(String cardContext) {
        this.cardContext = cardContext;
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

    public String getTranslationDirection() {
        return translationDirection;
    }

    public void setTranslationDirection(String translationDirection) {
        this.translationDirection = translationDirection;
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
