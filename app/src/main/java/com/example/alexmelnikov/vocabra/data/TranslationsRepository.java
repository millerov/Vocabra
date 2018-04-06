package com.example.alexmelnikov.vocabra.data;

import android.support.annotation.Nullable;
import android.transition.Transition;
import android.util.Log;

import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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
        translations = new ArrayList<Translation>(realm.where(Translation.class).findAll());
        return translations;
    }

    public ArrayList<Translation> getSortedTranslationsFromDB() {
        ArrayList<Translation> translations;
        Realm realm = Realm.getDefaultInstance();
        translations = new ArrayList<Translation>(realm.where(Translation.class)
                .sort("creationDate", Sort.ASCENDING)
                .findAll());
        return translations;
    }

    public Translation getTranslationByIdFromDB(int id) {
        Translation translation;
        Realm realm = Realm.getDefaultInstance();
        translation = realm.where(Translation.class)
                .equalTo("id", id)
                .findFirst();
        return translation;
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


    public Translation getSimilarElementInDB(Translation translation) {
        Translation similarTranslation;
        Realm realm = Realm.getDefaultInstance();
        similarTranslation = realm.where(Translation.class)
                .equalTo("fromText", translation.getFromText())
                .equalTo("toText", translation.getToText())
                .equalTo("langs", translation.getLangs())
                .findFirst();
        return similarTranslation;
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


    public void updateTranslationFavoriteStateDB(Translation translation, String fromText, String toText,
                                                 boolean favorite, @Nullable Card card) {
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

                if (favorite) {
                    Card managedCard = realm.where(Card.class)
                            .equalTo("front", card.getFront())
                            .equalTo("back", card.getBack())
                            .equalTo("translationDirection", card.getTranslationDirection())
                            .findFirst();

                    updatedTranslation.setCard(managedCard);
                } else {
                    updatedTranslation.setCard(null);

                }
            }
        });
        realm.close();
    }


    public Translation findTranslationByCardInDB(Card card) {
        RealmResults<Translation> similarTranslations;
        Realm realm = Realm.getDefaultInstance();
        Card translationCard;
        similarTranslations = realm.where(Translation.class)
                .equalTo("fromText", card.getFront())
                .equalTo("toText", card.getBack())
                .equalTo("langs", card.getTranslationDirection())
                .findAll();
        for (Translation t : similarTranslations) {
            try {
                translationCard = t.getCard();
            } catch (Exception e) {
                translationCard = null;
            }

            if (translationCard != null && translationCard.equals(card)) {
                return t;
            }
        }
        return null;
    }
}
