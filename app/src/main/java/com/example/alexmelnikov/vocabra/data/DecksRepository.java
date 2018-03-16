package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by AlexMelnikov on 14.03.18.
 */

public class DecksRepository {

    public void insertDeckToDB(Deck deck) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d("MyTag", "Updating realm db (Deck)");
                    int nextID;
                    try {
                        // Incrementing primary key manually
                        nextID = realm.where(Deck.class).max("id").intValue() + 1;
                    } catch (NullPointerException e) {
                        // If there is first item, being added to cache, give it id = 0
                        nextID = 0;
                    }
                    deck.setId(nextID);
                    realm.copyToRealmOrUpdate(deck);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public ArrayList<Deck> getDecksFromDB() {
        ArrayList<Deck> decks;
        Realm realm = Realm.getDefaultInstance();
        decks = new ArrayList(realm.where(Deck.class).findAll());
        return decks;
    }


    public boolean containsSimilarElementInDB(Deck deck) {
        final boolean[] result = new boolean[1];

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Deck> realmResult = realm.where(Deck.class)
                        .equalTo("name", deck.getName())
                        .findAll();
                if (realmResult.size() != 0)
                    result[0] = true;
                else result[0] = false;
            }
        });
        realm.close();
        return result[0];
    }


    public Deck findDeckByName(String name) {
        final Deck[] result = new Deck[1];
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Deck> realmResult = realm.where(Deck.class)
                        .equalTo("name", name)
                        .findAll();

                result[0] = realmResult.first();
            }
        });
        realm.close();
        return result[0];
    }

    public void clearDecksDB() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Deck.class);
            }
        });
        realm.close();
    }

}
