package com.example.alexmelnikov.vocabra.model;

import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public class Deck extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;

    private String color;

    private Language firstLanguage;

    private Language secondLanguage;

    private int numberOfCards;

    private int numberOfNewCards;

    private int numberOfCardsToTrain;

    @LinkingObjects("deck")
    private final RealmResults<Card> cards = null;

    public Deck() {}

    public Deck(int id, String name, String color, Language firstLanguage, Language secondLanguage) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.firstLanguage = firstLanguage;
        this.secondLanguage = secondLanguage;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Language getFirstLanguage() {
        return firstLanguage;
    }

    public void setFirstLanguage(Language firstLanguage) {
        this.firstLanguage = firstLanguage;
    }

    public Language getSecondLanguage() {
        return secondLanguage;
    }

    public void setSecondLanguage(Language secondLanguage) {
        this.secondLanguage = secondLanguage;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public int getNumberOfNewCards() {
        return numberOfNewCards;
    }

    public void setNumberOfNewCards(int numberOfNewCards) {
        this.numberOfNewCards = numberOfNewCards;
    }

    public int getNumberOfCardsToTrain() {
        return numberOfCardsToTrain;
    }

    public void setNumberOfCardsToTrain(int numberOfCardsToTrain) {
        this.numberOfCardsToTrain = numberOfCardsToTrain;
    }

    public RealmResults<Card> getCards() {
        return cards;
    }
}
