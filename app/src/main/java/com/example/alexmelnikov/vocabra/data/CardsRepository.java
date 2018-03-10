package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public class CardsRepository {

    public void insertCardToDB(Card card) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d("MyTag", "Updating realm db (Card)");
                    int nextID;
                    try {
                        // Incrementing primary key manually
                        nextID = realm.where(Card.class).max("id").intValue() + 1;
                    } catch (NullPointerException e) {
                        // If there is first item, being added to cache, give it id = 0
                        nextID = 0;
                    }
                    card.setId(nextID);
                    realm.copyToRealmOrUpdate(card);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public ArrayList<Card> getCardsFromDB() {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList(realm.where(Card.class).findAll());
        return cards;
    }

    public void clearCardsDB() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Card.class);
            }
        });
        realm.close();
    }
}
