package com.example.alexmelnikov.vocabra.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Common class.
 * Get used by repository, interactor and presenter without transformation.
 */
public class Translation extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;

    private String langs;

    private String fromText;

    private String toText;

    private boolean favorite;

    @Nullable
    private Card card;

    public Translation() {}

    public Translation(int id, String langs, String fromText, String toText, Boolean favorite) {
        this.id = id;
        this.langs = langs;
        this.fromText = fromText;
        this.toText = toText;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLangs() {
        return langs;
    }

    public void setLangs(String langs) {
        this.langs = langs;
    }

    public String getFromText() {
        return fromText;
    }

    public void setFromText(String fromText) {
        this.fromText = fromText;
    }

    public String getToText() {
        return toText;
    }

    public void setToText(String toText) {
        this.toText = toText;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
