package com.example.alexmelnikov.vocabra.model.temp;

import android.support.annotation.Nullable;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.Date;

/**
 * Created by AlexMelnikov on 18.03.18.
 */

public class TemporaryTranslation {

    private String langs;

    private String fromText;

    private String toText;

    private boolean favorite;

    @Nullable
    private Card card;

    private Date creationDate;

    public TemporaryTranslation(Translation translation) {
        this.langs = translation.getLangs();
        this.fromText = translation.getFromText();
        this.toText = translation.getToText();
        this.favorite = translation.getFavorite();
        this.card = translation.getCard();
        this.creationDate = translation.getCreationDate();
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}

