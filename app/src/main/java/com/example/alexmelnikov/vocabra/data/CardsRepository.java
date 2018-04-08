package com.example.alexmelnikov.vocabra.data;

import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.CardSortMethod;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        cards = new ArrayList<Card>(realm.where(Card.class).findAll());
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

    public Card getCardByIdDB(int id) {
        Card card;
        Realm realm = Realm.getDefaultInstance();
        card = realm.where(Card.class)
                .equalTo("id", id)
                .findFirst();
        return card;
    }


    public void updateReadyStatusForAllCards() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@Nonnull Realm realm) {
                ArrayList<Card> cards = new ArrayList<Card>(realm.where(Card.class).findAll());
                Date currentDate = new Date();
                for (Card c : cards)
                    if (c.getNextTimeForTraining() != null)
                        if (c.getNextTimeForTraining().before(currentDate))
                            c.setReadyForTraining(true);
                        else
                            c.setReadyForTraining(false);
            }
        });
        realm.close();
    }


    public void updateReadyStatusForCardsInDeck(Deck deck) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ArrayList<Card> cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .findAll());
                Date currentDate = new Date();
                for (Card c : cards)
                    if (c.getNextTimeForTraining() != null)
                        if (c.getNextTimeForTraining().before(currentDate))
                            c.setReadyForTraining(true);
                        else
                            c.setReadyForTraining(false);
            }
        });
        realm.close();
    }



    public ArrayList<Card> getReadyCardsByDeckDB(Deck deck) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList<Card>(realm.where(Card.class)
                .equalTo("deck.name", deck.getName())
                .equalTo("isReadyForTraining", true)
                .findAll());
        return cards;
    }

    public ArrayList<Card> getNewCardsDB() {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList<Card>(realm.where(Card.class)
                .equalTo("isNew", true)
                .findAll());
        return cards;
    }

    public ArrayList<Card> getOldReadyForTrainCardsDB() {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList<Card>(realm.where(Card.class)
                .equalTo("isNew", false)
                .equalTo("isReadyForTraining", true)
                .findAll());
        return cards;
    }

    public ArrayList<Card> getNewCardsByDeckDB(Deck deck) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList<Card>(realm.where(Card.class)
                .equalTo("deck.name", deck.getName())
                .equalTo("isNew", true)
                .findAll());
        return cards;
    }

    public ArrayList<Card> getOldReadyForTrainCardsByDeckDB(Deck deck) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        cards = new ArrayList<Card>(realm.where(Card.class)
                .equalTo("deck.name", deck.getName())
                .equalTo("isNew", false)
                .equalTo("isReadyForTraining", true)
                .findAll());
        return cards;
    }



    public boolean containsSimilarCardInDeckDB(Card card, Deck deck) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Card> similarCards = realm.where(Card.class)
                .equalTo("front", card.getFront())
                .equalTo("back", card.getBack())
                .equalTo("translationDirection", card.getTranslationDirection())
                .equalTo("cardContext", card.getCardContext())
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
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("creationDate", Sort.ASCENDING)
                        .findAll());
            } else {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("creationDate", Sort.DESCENDING)
                        .findAll());
            }
        } else if (method.getId() == 1) {
            if (method.isAscending()) {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("timesTrained", Sort.ASCENDING)
                        .findAll());
            } else {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("timesTrained", Sort.DESCENDING)
                        .findAll());
            }
        } else if (method.getId() == 2) {
            if (method.isAscending()) {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("lastTimeTrained", Sort.ASCENDING)
                        .findAll());
            } else {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .sort("lastTimeTrained", Sort.DESCENDING)
                        .findAll());
            }
        } else cards = null;
        return cards;
    }

    public ArrayList<Card> getSortedCardsByDeckDB(Deck deck, CardSortMethod method) {
        ArrayList<Card> cards;
        Realm realm = Realm.getDefaultInstance();
        if (method.getId() == 0) {
            if (method.isAscending()) {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("creationDate", Sort.ASCENDING)
                        .findAll());
            } else {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("creationDate", Sort.DESCENDING)
                        .findAll());
            }
        } else if (method.getId() == 1) {
            if (method.isAscending()) {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("timesTrained", Sort.ASCENDING)
                        .findAll());
            } else {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("timesTrained", Sort.DESCENDING)
                        .findAll());
            }
        } else if (method.getId() == 2) {
            if (method.isAscending()) {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("lastTimeTrained", Sort.ASCENDING)
                        .findAll());
            } else {
                cards = new ArrayList<Card>(realm.where(Card.class)
                        .equalTo("deck.name", deck.getName())
                        .sort("lastTimeTrained", Sort.DESCENDING)
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

    public void resetCardTrainingStats(Card card) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Card updatedCard = realm.where(Card.class)
                        .equalTo("id", card.getId())
                        .findFirst();
                updatedCard.setReadyForTraining(true);
                updatedCard.setNew(true);
                updatedCard.setTimesTrained(0);
                updatedCard.setLevel(1);
                updatedCard.setLastIncrement(0);
                updatedCard.setNextTimeForTraining(new Date());
                updatedCard.setLastTimeTrained(null);
            }
        });
        realm.close();
    }

    /** @param  incrementDays equals -1 when we don't want to update current value
     *                                  (e. g. cards has been forgotten)          */
    public void updateCardAfterTraining(Card card, Date nextTimeForTraining, int newLevel, int incrementDays) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Card updatedCard = realm.where(Card.class)
                        .equalTo("id", card.getId())
                        .findFirst();
                if (updatedCard.isNew())
                    updatedCard.setNew(false);
                updatedCard.setLastTimeTrained(new Date());
                updatedCard.setNextTimeForTraining(nextTimeForTraining);
                updatedCard.setLevel(newLevel);
                if (incrementDays != -1)
                    updatedCard.setLastIncrement(incrementDays);
                updatedCard.setTimesTrained(updatedCard.getTimesTrained() + 1);
            }
        });
        realm.close();
    }

    public void updateCardAfterReturnUsingOldVirsionOfCard(Card card, boolean isNew, Date lastTimeTrained,
                                                           Date nextTimeForTraining, int level) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Card updatedCard = realm.where(Card.class)
                        .equalTo("id", card.getId())
                        .findFirst();

                updatedCard.setNew(isNew);
                updatedCard.setLastTimeTrained(lastTimeTrained);
                updatedCard.setNextTimeForTraining(nextTimeForTraining);
                updatedCard.setLevel(level);
                updatedCard.setTimesTrained(card.getTimesTrained() - 1);
            }
        });
        realm.close();
    }
}
