package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

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

    public void deleteTranslationFromDB(Translation translation) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Translation> result = realm.where(Translation.class)
                        .equalTo("fromText", translation.getFromText())
                        .equalTo("toText", translation.getToText())
                        .findAll();
                result.deleteAllFromRealm();
            }
        });
        realm.close();
    }


    public boolean containsSimilarElementInDB(Translation translation) {
        final boolean[] result = new boolean[1];

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Translation> realmResult = realm.where(Translation.class)
                        .equalTo("fromText", translation.getFromText())
                        .equalTo("toText", translation.getToText())
                        .findAll();
                Log.d("similar", "execute: " + realmResult.size());
                if (realmResult.size() != 0)
                    result[0] = true;
                else result[0] = false;
            }
        });
        realm.close();
        return result[0];
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

    //SHOULD WORK, if not uncomment line 112
    public void updateTranslationFavoriteStateDB(Translation translation, String fromText, String toText,
                                                 boolean favorite) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Translation updatedTranslation = realm.where(Translation.class)
                        .equalTo("id", translation.getId())
                        .findFirst();
                updatedTranslation.setFavorite(favorite);
                updatedTranslation.setFromText(fromText);
                updatedTranslation.setToText(toText);
              //  realm.copyToRealmOrUpdate(translation);
            }
        });
        realm.close();
    }

}
