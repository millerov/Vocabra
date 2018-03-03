package com.example.alexmelnikov.vocabra.model;

import android.support.annotation.NonNull;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlexMelnikov on 01.03.18.
 */

public class Language extends RealmObject implements Comparable<Language> {

    @PrimaryKey
    private String id;
    private String lang;

    public Language() {

    }

    public Language(String id, String lang){
        this.id = id;
        this.lang = lang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public int compareTo(@NonNull Language language) {
        return (this.getLang()).compareTo(language.getLang());
    }
}
