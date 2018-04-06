package com.example.alexmelnikov.vocabra.ui.translator;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.adapter.HistoryRecyclerItemTouchHelper;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.SelectedLanguages;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.model.temp.TemporaryCard;
import com.example.alexmelnikov.vocabra.model.temp.TemporaryTranslation;
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;
import com.example.alexmelnikov.vocabra.ui.Translating;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 27.02.18.
 */

@InjectViewState
public class TranslatorPresenter extends MvpPresenter<TranslatorView> implements Translating, SnackBarActionHandler, HistoryRecyclerItemTouchHelper.HistoryItemTouchHelperListener {

    private static final String TAG = "MyTag";

    @Inject
    LanguagesRepository mLangRep;
    @Inject
    TranslationsRepository mTransRep;
    @Inject
    UserDataRepository mUserData;
    @Inject
    CardsRepository mCardsRep;
    @Inject
    DecksRepository mDecksRep;

    ArrayList<Language> mLangList;

    private int mSelectedFrom; //TranslatonFragment spinner index
    private int mSelectedTo; //TranslationFragment spinner index

    private String mSelectedToLanguage; //e.g. "Английский"
    private String mSelectedFromLanguage; //e.g. "Русский"

    private String mInput = "";
    private String mOutput = "";

    private Translation mLastLoadedTranslation;

    private ArrayList<Translation> mHistoryList;

    // this 3 variables initialized when user deletes translation from favorites (deletes card)
    private TemporaryCard mTemporaryCard;
    private Translation mTemporaryCardTranslation;
    private int mTemporaryCardPositionInHistory;

    // this 3 variables initialized when user deletes translation
    private int tempTranslationIndex;
    private TemporaryTranslation mTemporaryTranslation;
    private ArrayList<TemporaryTranslation> mTemporaryTranslations;


    public TranslatorPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        mLangList = mLangRep.getLanguagesFromDB();
        Collections.sort(mLangList);

        SelectedLanguages selectedLanguages = (SelectedLanguages) mUserData.getValue(mUserData.SELECTED_LANGUAGES, new SelectedLanguages(
                mLangList.indexOf(LanguageUtils.findByKey("ru")),
                mLangList.indexOf(LanguageUtils.findByKey("en"))));

        Log.d(TAG, "TranslatorPresenter: " + selectedLanguages.to() + "-" + selectedLanguages.from());

        mSelectedFrom = selectedLanguages.from();
        mSelectedTo = selectedLanguages.to();

        mTemporaryTranslations = new ArrayList<TemporaryTranslation>();

        updateSelectedLanguages();
    }

    @Override
    public void attachView(TranslatorView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        getViewState().setupSpinners(mLangList, mSelectedFrom, mSelectedTo);
        getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
        loadHistoryData();
    }

    @Override
    public void detachView(TranslatorView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public ArrayList<Language> getLanguages() {
        return mLangRep.getLanguagesFromDB();
    }


    public void setInputOutput(Translation translation) {
        if (translation != null && translation.getFromText() != null) {
            mLastLoadedTranslation = translation;
            mInput = translation.getFromText();
            mOutput = translation.getToText();
            if (mOutput != "" || mInput == mOutput)
                updateDatabase();
            getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
            getViewState().showTranslationCard();
        } else {
            mInput = "";
            mOutput = "";
            getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
        }
    }

    public void translationRequested(String data) {
        try {
            VocabraApp.getApiHelper().translateAsync(data, TextUtils.getTranslationDir(mSelectedFromLanguage, mSelectedToLanguage), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void translationResultPassed(Translation nextTranslation) {
        mLastLoadedTranslation = nextTranslation;
        mOutput = nextTranslation.getToText();
        getViewState().showTranslationResult(mOutput);
        getViewState().showTranslationCard();
        updateDatabase();
        loadHistoryData();
    }

    public void translationResultError() {
        getViewState().showTranslationResult("Текст не может быть переведен");
        getViewState().showTranslationCard();
    }

    public void selectorFrom(int index) {
        if (index == mSelectedTo) {
            swapSelection();
        } else {
            mSelectedFrom = index;
            updateSelectedLangsIndexes();
            updateSelectedLanguages();
            getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
            if (!mInput.isEmpty() && mLastLoadedTranslation != null) {
                translationRequested(mInput);
            }
        }
    }

    public void selectorTo(int index) {
        if (index == mSelectedFrom) {
            swapSelection();
        } else {
            mSelectedTo = index;
            updateSelectedLangsIndexes();
            updateSelectedLanguages();
            getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
            if (!mInput.isEmpty() && mLastLoadedTranslation != null) {
                translationRequested(mInput);
            }
        }
    }

    public void clearButtonPressed() {
        if (!mInput.isEmpty()) {
            mLastLoadedTranslation = null;
            mInput = mOutput = "";
            getViewState().clearInputOutput();
            getViewState().hideTranslationCard();
        }
    }


    public void swapSelection() {
        int temp = mSelectedFrom;
        mSelectedFrom = mSelectedTo;
        mSelectedTo = temp;
        getViewState().changeLanguagesSelected(mSelectedFrom, mSelectedTo);

        if (!mOutput.isEmpty()) {
            String tempStr = mInput;
            mInput = mOutput;
            mOutput = tempStr;
        }

        updateSelectedLangsIndexes();
        updateSelectedLanguages();
        //getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
        if (!mInput.isEmpty())
            translationRequested(mInput);
    }

    public void inputRequested() {
        getViewState().openTranslationFragment(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
    }

    public void copyButtonPressed() {
        getViewState().copyAction(mOutput);
    }


    public void translationCardFavButtonPressed() {
        if (!mHistoryList.get(mHistoryList.size() - 1).getFavorite())
            addNewCardFromCurrentTranslationRequest();
        else {
            dropFavoriteStatusRequest(mHistoryList.size() - 1);
            getViewState().changeFavouriteButtonAppearance(false);
        }
    }

    private void addNewCardFromCurrentTranslationRequest() {
        getViewState().showAddCardDialog(-1, mLastLoadedTranslation,
                mDecksRep.findDecksByTranslationDirection(mLastLoadedTranslation.getLangs()));
    }


    public void addNewCardFromHistoryRequest(int pos) {
        getViewState().showAddCardDialog(pos, mHistoryList.get(pos),
                mDecksRep.findDecksByTranslationDirection(mHistoryList.get(pos).getLangs()));
    }


    public void addNewCardFromHistoryResultPassed(int pos, Translation initialTranslation, String front,
                                                  String back, String cardContext, String chosenDeckName, int defaultColor) {

        String firstLanguageName = LanguageUtils.findNameByKey(TextUtils.getFirstLanguageIndexFromDir(initialTranslation.getLangs()));
        String secondLanguageName = LanguageUtils.findNameByKey(TextUtils.getSecondLanguageIndexFromDir(initialTranslation.getLangs()));
        Language firstLanguage = LanguageUtils.findByName(firstLanguageName);
        Language secondLanguage = LanguageUtils.findByName(secondLanguageName);
        Deck deck;

        //Check which deck option has been chosen
        if (!chosenDeckName.equals("По умолчанию")) {
            deck = mDecksRep.getDeckByName(chosenDeckName);
        } else {
            String defaultDeckName = firstLanguageName + "-" + secondLanguageName;
            //if there hasn't yet default deck for this language direction been created then create one
            if (mDecksRep.getDeckByName(defaultDeckName) == null) {
                deck = new Deck(-1, defaultDeckName, defaultColor, firstLanguage,
                        secondLanguage);
                mDecksRep.insertDeckToDB(deck);
            } else {
                deck = mDecksRep.getDeckByName(defaultDeckName);
            }
        }

        Card card = new Card(-1, front, back, firstLanguage, secondLanguage, deck, cardContext);
        mCardsRep.insertCardToDB(card);

        /*check if translation from db texts equal to set by user front and back
        if not, update translation element in db
        also update translation element favourite status and update recycler element on the fragment*/
        if (initialTranslation.getFromText().equals(front)
                && initialTranslation.getToText().equals(back)) {
            mTransRep.updateTranslationFavoriteStateDB(initialTranslation, initialTranslation.getFromText(),
                    initialTranslation.getToText(), true, card);
        } else {
            mTransRep.updateTranslationFavoriteStateDB(initialTranslation, front, back, true, card);
        }

        getViewState().updateHistoryDataElement(pos, mTransRep.getTranslationByIdFromDB(initialTranslation.getId()));


        getViewState().changeFavouriteButtonAppearance(true);

    }

    public void dropFavoriteStatusRequest(int pos) {
        Translation affectedTranslation = mHistoryList.get(pos);
        Card card = affectedTranslation.getCard();

        mTemporaryCard = new TemporaryCard(card.getFront(), card.getBack(), card.getCardContext(), card.getTranslationDirection(),
                card.getFrontLanguage(), card.getBackLanguage(), card.getDeck(), card.isReadyForTraining(), card.getLastTimeTrained(),
                card.getTimesTrained(), card.isNew(), card.getNextTimeForTraining(), card.getLevel());
        mTemporaryCardTranslation = affectedTranslation;
        mTemporaryCardPositionInHistory = pos;

        mCardsRep.deleteCardFromDB(affectedTranslation.getCard());
        mTransRep.updateTranslationFavoriteStateDB(affectedTranslation, affectedTranslation.getFromText(),
                affectedTranslation.getToText(), false, null);
        getViewState().updateHistoryDataElement(pos, affectedTranslation);
        getViewState().showFavoriteDropMessage();
        getViewState().changeFavouriteButtonAppearance(false);
    }


    /** @param actionId = 1 – Undo card deletion
     *                  = 2 – Undo one translation deletion from history
     *                  = 3 – Undo history cleaning */
    @Override
    public void onSnackbarEvent(int actionId) {
        //undo card deletion
        if (actionId == 1) {
            Card card = new Card(-1, mTemporaryCard);
            mCardsRep.insertCardToDB(card);
            mTransRep.updateTranslationFavoriteStateDB(mTemporaryCardTranslation,
                    mTemporaryCardTranslation.getFromText(),
                    mTemporaryCardTranslation.getToText(),
                    true, card);

            getViewState().updateHistoryDataElement(mTemporaryCardPositionInHistory, mTemporaryCardTranslation);
            getViewState().changeFavouriteButtonAppearance(true);

        } else if (actionId == 2) {
           Translation translation = new Translation(-1, mTemporaryTranslation);
           mTransRep.insertTranslationToDB(translation);
           if (translation.getCard() != null)
           loadHistoryData();
        } else if (actionId == 3) {
            for (TemporaryTranslation tt : mTemporaryTranslations) {
                Translation translation = new Translation(-1, tt);
                mTransRep.insertTranslationToDB(translation);
            }
            loadHistoryData();
        }
    }


    public void historyItemLongClicked(int pos) {
        getViewState().showDeleteOptionsDialog(pos);
    }


    public void deleteDialogOptionPicked(int pos, int option) {
        switch (option) {
            case 0:
                Translation translationForDelete = mHistoryList.get(pos);
                tempTranslationIndex = mHistoryList.size() - pos - 1;
                mTemporaryTranslation = new TemporaryTranslation(translationForDelete);
                mTransRep.deleteTranslationFromDB(translationForDelete);
                loadHistoryData();
                getViewState().showItemDeletedFromHistoryMessage();
                break;
            case 1:
                mTemporaryTranslations.clear();
                for (Translation t : mHistoryList)
                    mTemporaryTranslations.add(new TemporaryTranslation(t));
                mTransRep.clearTranslationsDB();
                loadHistoryData();
                getViewState().showHistoryCleanedMessage();
                break;
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Translation translationForDelete = mHistoryList.get(mHistoryList.size() - position - 1);
        Log.d(TAG, "onSwiped: " + position + "//" + (mHistoryList.size() - position - 1));
        Log.d(TAG, "onSwiped: " + translationForDelete.getFromText() + "/" + translationForDelete.getToText());
        tempTranslationIndex = position;
        mTemporaryTranslation = new TemporaryTranslation(translationForDelete);
        if (mTemporaryTranslation.getCard() != null)
            Log.d(TAG, "onSwiped: " + mTemporaryTranslation.getCard().getFront() + "/" + mTemporaryTranslation.getCard().getBack());
        mTransRep.deleteTranslationFromDB(translationForDelete);
        loadHistoryData();
        getViewState().showItemDeletedFromHistoryMessage();
    }

    //==================Private logic=================

    private void loadHistoryData() {
        mHistoryList = mTransRep.getSortedTranslationsFromDB();

        getViewState().replaceHistoryData(mHistoryList);
    }

    private void updateSelectedLangsIndexes() {
        SelectedLanguages newValue = new SelectedLanguages(mSelectedFrom, mSelectedTo);
        mUserData.putValue(mUserData.SELECTED_LANGUAGES, newValue);
    }

    private void updateSelectedLanguages() {
        try {
            mSelectedToLanguage = mLangList.get(mSelectedTo).getLang();
            mSelectedFromLanguage = mLangList.get(mSelectedFrom).getLang();
        } catch (Exception e) {
            mSelectedToLanguage = mSelectedFromLanguage = "Error";
        }
    }

    private void updateDatabase() {

        /*check if db already contains similar translation, delete it if yes.
        It's done to keep new translation on the top of history recyclerview*/
        Translation similarTranslationFromDB = mTransRep.getSimilarElementInDB(mLastLoadedTranslation);
        if (similarTranslationFromDB == null) {
            mTransRep.insertTranslationToDB(mLastLoadedTranslation);
        } else {
            if (similarTranslationFromDB.getFavorite()) {
                Card card = similarTranslationFromDB.getCard();
                mTransRep.deleteTranslationFromDB(similarTranslationFromDB);
                mTransRep.insertTranslationToDB(mLastLoadedTranslation);
                mTransRep.updateTranslationFavoriteStateDB(mLastLoadedTranslation, mLastLoadedTranslation.getFromText(),
                        mLastLoadedTranslation.getToText(), true, card);
                getViewState().changeFavouriteButtonAppearance(true);
            } else {
                mTransRep.deleteTranslationFromDB(mLastLoadedTranslation);
                mTransRep.insertTranslationToDB(mLastLoadedTranslation);
            }
        }
    }

}
