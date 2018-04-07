package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.adapter.DecksDialogAdapter$DecksViewHolder_ViewBinding;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.DailyStats;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

public class StatisticsRepository {

    private static final String TAG = "MyTag";

    public void insertStatisticsToDB(DailyStats stats) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int nextID;
                    try {
                        // Incrementing primary key manually
                        nextID = realm.where(Card.class).max("id").intValue() + 1;
                    } catch (NullPointerException e) {
                        // If there is first item, being added to cache, give it id = 0
                        nextID = 0;
                    }
                    stats.setId(nextID);
                    realm.copyToRealmOrUpdate(stats);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public ArrayList<DailyStats> getStatisticsFromDB() {
        ArrayList<DailyStats> stats;
        Realm realm = Realm.getDefaultInstance();
        stats = new ArrayList<DailyStats>(realm.where(DailyStats.class).findAll());
        return stats;
    }


    public void fillStatisticsUpToDate(DateTime firstAppLaunchDate) {
        DateTime currentDateTime = new DateTime();

        Realm realm = Realm.getDefaultInstance();
        while (firstAppLaunchDate.isBefore(currentDateTime) || firstAppLaunchDate.withTimeAtStartOfDay().equals(currentDateTime.withTimeAtStartOfDay())) {
            DailyStats stats = realm.where(DailyStats.class)
                    .equalTo("stringDate", LocalDate.fromDateFields(firstAppLaunchDate.toDate()).toString())
                    .findFirst();
            if (stats == null) {
                DailyStats newStats = new DailyStats(LocalDate.fromDateFields(firstAppLaunchDate.toDate()));
                insertStatisticsToDB(newStats);
            }
            firstAppLaunchDate = firstAppLaunchDate.plusDays(1);
        }
    }


    public void increaseTodayCardsTrainedCounter() {
        String today = new LocalDate().toString();
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DailyStats stats = realm.where(DailyStats.class)
                        .equalTo("stringDate", today)
                        .findFirst();
                if (stats == null) {
                    DailyStats newStats = new DailyStats(new LocalDate());
                    newStats.setCardsTrained(1);
                    realm.insertOrUpdate(newStats);
                } else {
                    stats.setCardsTrained(stats.getCardsTrained() + 1);
                }

            }
        });
        realm.close();
    }
}
