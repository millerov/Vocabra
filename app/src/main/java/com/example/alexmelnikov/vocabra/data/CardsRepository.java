package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.CardSortMethod;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public class CardsRepository {

    private static final String TAG = "MyTag";

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

    public ArrayList<Card> getCardsByDeckDB(Deck deck) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList<Card>(realm.where(Card.class)
                .equalTo("deck.name", deck.getName())
                .findAll());
        return cards;
    }

    public boolean containsSimilarCardInDeckDB(Card card, Deck deck) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Card> similarCards = realm.where(Card.class)
                .equalTo("front", card.getFront())
                .equalTo("back", card.getBack())
                .equalTo("translationDirection", card.getTranslationDirection())
                .findAll();
        for (int i = 0; i < similarCards.size(); i++) {
            if (similarCards.get(i).getDeck().getName().equals(deck.getName()))
                return true;
        }
        return false;
    }

    public void deleteCardFromDB(Card card) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Card> result = realm.where(Card.class).equalTo("id", card.getId()).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    public void returnPreviouslyDeletedCard(Card card) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(card);
            }
        });
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

    public ArrayList<Card> getSortedCardsDB(CardSortMethod method) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();

        if (method.getId() == 0) {
            if (method.isAscending()) {
                /*Log.d(TAG, "getSortedCardsDB: sorting ascending");
                result.sort("id", Sort.ASCENDING);*/
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("creationDate", Sort.ASCENDING)
                        .findAll());
            } else {
                /*Log.d(TAG, "getSortedCardsDB: sorting descending");
                result.sort("id", Sort.DESCENDING);*/
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("creationDate", Sort.DESCENDING)
                        .findAll());
            }
        } else cards = null; /*else if (method.getId() == 1) {
            if (method.isAscending())
                result.sort("timesTrained", Sort.ASCENDING);
            else
                result.sort("timesTrained", Sort.DESCENDING);
        } else if (method.getId() ==2 ) {
            if (method.isAscending())
                result.sort("lastTimeTrained", Sort.ASCENDING);
            else
                result.sort("lastTimeTrained", Sort.DESCENDING);
        }*/

//        cards = new ArrayList<Card>(result);
        return cards;
    }

    public ArrayList<Card> getSortedCardsByDeckDB(Deck deck, CardSortMethod method) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        if (method.getId() == 0) {
            if (method.isAscending()) {
                /*Log.d(TAG, "getSortedCardsDB: sorting ascending");
                result.sort("id", Sort.ASCENDING);*/
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("creationDate", Sort.ASCENDING)
                        .findAll());
            } else {
                /*Log.d(TAG, "getSortedCardsDB: sorting descending");
                result.sort("id", Sort.DESCENDING);*/
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("creationDate", Sort.DESCENDING)
                        .findAll());
            }
        } else cards = null;
        return cards;
    }

    public void updateCardInDB(Card card, String front, String back, String cardContext,
                           Deck deck) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Card updatedCard = realm.where(Card.class)
                        .equalTo("id", card.getId())
                        .findFirst();
                updatedCard.setFront(front);
                updatedCard.setBack(back);
                updatedCard.setCardContext(cardContext);
                updatedCard.setDeck(deck);
            }
        });
        realm.close();
    }
}
