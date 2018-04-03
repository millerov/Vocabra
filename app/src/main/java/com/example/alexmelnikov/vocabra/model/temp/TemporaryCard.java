package com.example.alexmelnikov.vocabra.model.temp;

import android.support.annotation.Nullable;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;

import java.util.Date;

/**
 * This class is used when user deletes card but chooses to undo action
 * with the help of snackbar button
 */

public class TemporaryCard {

    @Nullable
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

    private boolean isNew;
    private Date nextTimeForTraining;
    private int level;

    public TemporaryCard(String front, String back, String cardContext, String translationDirection, Language frontLanguage, Language backLanguage,
                         Deck deck, boolean isReadyForTraining, Date lastTimeTrained, int timesTrained,
                         boolean isNew, Date nextTimeForTraining, int level) {
        id = -1;
        this.front = front;
        this.back = back;
        this.cardContext = cardContext;
        this.translationDirection = translationDirection;
        this.frontLanguage = frontLanguage;
        this.backLanguage = backLanguage;
        this.deck = deck;
        this.isReadyForTraining = isReadyForTraining;
        this.lastTimeTrained = lastTimeTrained;
        this.timesTrained = timesTrained;
        this.isNew = isNew;
        this.nextTimeForTraining = nextTimeForTraining;
        this.level = level;
    }

    public TemporaryCard(Card card) {
        this.id = card.getId();
        this.front = card.getFront();
        this.back = card.getBack();
        this.cardContext = card.getCardContext();
        this.translationDirection = card.getTranslationDirection();
        this.frontLanguage = card.getFrontLanguage();
        this.backLanguage = card.getBackLanguage();
        this.deck = card.getDeck();
        this.isReadyForTraining = card.isReadyForTraining();
        this.lastTimeTrained = card.getLastTimeTrained();
        this.timesTrained = card.getTimesTrained();
        this.isNew = card.isNew();
        this.nextTimeForTraining = card.getNextTimeForTraining();
        this.level = card.getLevel();
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

    public String getTranslationDirection() {
        return translationDirection;
    }

    public void setTranslationDirection(String translationDirection) {
        this.translationDirection = translationDirection;
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public Date getNextTimeForTraining() {
        return nextTimeForTraining;
    }

    public void setNextTimeForTraining(Date nextTimeForTraining) {
        this.nextTimeForTraining = nextTimeForTraining;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Nullable
    public int getId() {
        return id;
    }

    public void setId(@Nullable int id) {
        this.id = id;
    }
}
