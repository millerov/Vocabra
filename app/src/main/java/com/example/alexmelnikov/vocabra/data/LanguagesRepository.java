package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Language;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by AlexMelnikov on 01.03.18.
 */

public class LanguagesRepository {

    public void insertLanguagesToDB(ArrayList<Language> data) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d("MyTag", "Updating realm db");
                    for (Language lang : data) {
                        Log.d("db", "insert lang into db: " + lang.getId());
                        realm.copyToRealmOrUpdate(lang);
                    }
                }
            });
        } finally {
            if(realm != null) {
                realm.close();
            }
        }

    }

    public ArrayList<Language> getLanguagesFromDB() {

        ArrayList<Language> langList;
        Realm realm = Realm.getDefaultInstance();

        langList = new ArrayList(realm.where(Language.class).findAll());
        return langList;
    }

    public void clearDB() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Language.class);
            }
        });
        realm.close();
    }
}
