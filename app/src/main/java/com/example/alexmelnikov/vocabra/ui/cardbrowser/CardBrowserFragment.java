package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.CardsAdapter;
import com.example.alexmelnikov.vocabra.adapter.DecksDialogAdapter;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;


/**
 * Created by AlexMelnikov on 24.02.18.
 */


public class CardBrowserFragment extends BaseFragment implements CardBrowserView {

    private static final String TAG = "MyTag";

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "cardbrowser")
    CardBrowserPresenter mCardBrowserPresenter;

    @BindView(R.id.btn_decks) ImageButton btnDecks;
    @BindView(R.id.rv_cards) RecyclerView rvCards;


    private CardsAdapter mCardsAdapter;

    RecyclerView rvDecks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_browser, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardsAdapter = new CardsAdapter(getActivity(), new ArrayList<Card>());
        rvCards.setAdapter(mCardsAdapter);
    }

    @Override
    public void attachInputListeners() {
        Disposable decksButton = RxView.clicks(btnDecks)
                .subscribe(o -> mCardBrowserPresenter.decksButtonPressed());

        mDisposable.addAll(decksButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void replaceCardsRecyclerData(ArrayList<Card> cards) {
        mCardsAdapter.replaceData(cards);
    }

    @Override
    public void updateCardsRecyclerData(ArrayList<Card> cards) {
        mCardsAdapter.notifyItemInserted(cards.size() - 1);
    }

    @Override
    public void showDecksListDialog(ArrayList<Deck> decks) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Колоды")
                .customView(R.layout.dialog_decks, false)
                .positiveText("Добавить колоду")
                .negativeText("Назад")
                .onPositive(((dialog1, which) -> mCardBrowserPresenter.createNewDeckRequest()))
                .build();

        Log.d(TAG, "showDecksListDialog: " + decks.size());
        rvDecks = (RecyclerView) dialog.getView().findViewById(R.id.rv_decks);
        rvDecks.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDecks.setAdapter(new DecksDialogAdapter(getActivity(), decks));

        dialog.show();
    }
}
