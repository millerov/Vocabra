package com.example.alexmelnikov.vocabra.model;

import android.support.annotation.Nullable;

import com.example.alexmelnikov.vocabra.model.temp.TemporaryCard;

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

    @Nullable
    private Deck deck;

    private boolean isReadyForTraining;

    private boolean isNew;

    @Nullable
    private Date lastTimeTrained;

    private Date creationDate;

    private int timesTrained;

    public Card() {}

    //=======================
    //ADD DECK TO CONSTRUCTOR
    //=======================
    public Card(int id, String front, String back, Language frontLanguage,
                    Language backLanguage, @Nullable Deck deck, String cardContext) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.frontLanguage = frontLanguage;
        this.backLanguage = backLanguage;
        this.deck = deck;
        this.cardContext = cardContext;
        this.isReadyForTraining = true;
        this.timesTrained = 0;
        this.translationDirection = frontLanguage.getId() + "-" + backLanguage.getId();
        creationDate = new Date();
        isReadyForTraining = isNew = true;
        timesTrained = 0;
    }

    public Card(int id, TemporaryCard tempCard) {
        this.id = id;
        this.front = tempCard.getFront();
        this.back = tempCard.getBack();
        this.frontLanguage = tempCard.getFrontLanguage();
        this.backLanguage = tempCard.getBackLanguage();
        this.deck = tempCard.getDeck();
        this.cardContext = tempCard.getCardContext();
        this.isReadyForTraining = tempCard.isReadyForTraining();
        this.timesTrained = tempCard.getTimesTrained();
        this.translationDirection = tempCard.getTranslationDirection();
        this.lastTimeTrained = tempCard.getLastTimeTrained();
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
