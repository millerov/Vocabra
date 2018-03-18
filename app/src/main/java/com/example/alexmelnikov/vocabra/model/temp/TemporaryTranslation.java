package com.example.alexmelnikov.vocabra.model.temp;

/**
 * Created by AlexMelnikov on 18.03.18.
 */

public class TemporaryTranslation {

    private String langs;

    private String fromText;

    private String toText;

    private boolean favorite;

    public TemporaryTranslation(String langs, String fromText, String toText, boolean favorite) {
        this.langs = langs;
        this.fromText = fromText;
        this.toText = toText;
        this.favorite = favorite;
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
}
