package com.example.alexmelnikov.vocabra.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlexMelnikov on 01.03.18.
 */

public class Language extends RealmObject {

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
}
