package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;

import java.util.ArrayList;

import io.realm.Realm;

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
