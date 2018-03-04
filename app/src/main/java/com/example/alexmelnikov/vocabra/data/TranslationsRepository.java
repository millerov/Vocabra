package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by AlexMelnikov on 04.03.18.
 */

public class TranslationsRepository {

    public void insertTranslationToDB(Translation translation) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d("MyTag", "Updating realm db (Translation)");
                    int nextID;
                    try {
                        // Incrementing primary key manually
                        nextID = realm.where(Translation.class).max("id").intValue() + 1;
                    } catch (NullPointerException e) {
                        // If there is first item, being added to cache, give it id = 0
                        nextID = 0;
                    }
                    translation.setId(nextID);
                    realm.copyToRealmOrUpdate(translation);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public ArrayList<Translation> getTranslationsFromDB() {
        ArrayList<Translation> translations;
        Realm realm = Realm.getDefaultInstance();
        translations = new ArrayList(realm.where(Translation.class).findAll());
        return translations;
    }


    public void clearTranslationsDB() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Translation.class);
            }
        });
        realm.close();
    }

}
