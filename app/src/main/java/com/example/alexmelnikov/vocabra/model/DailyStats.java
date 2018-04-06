package com.example.alexmelnikov.vocabra.model;


import android.util.Log;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

public class DailyStats extends RealmObject {

    @PrimaryKey
    private int id;

    private String stringDate;

    private int cardsTrained;

    public DailyStats() {}

    public DailyStats(LocalDate date) {
        stringDate = date.toString();
        Log.d("MyTag", "DailyStats: " + stringDate);
        cardsTrained = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public int getCardsTrained() {
        return cardsTrained;
    }

    public void setCardsTrained(int cardsTrained) {
        this.cardsTrained = cardsTrained;
        Log.d("MyTag", "setCardsTrained: " + this.cardsTrained);
    }
}
