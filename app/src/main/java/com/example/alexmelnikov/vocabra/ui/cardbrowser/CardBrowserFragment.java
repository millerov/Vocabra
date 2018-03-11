package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.CardsAdapter;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by AlexMelnikov on 24.02.18.
 */


public class CardBrowserFragment extends BaseFragment implements CardBrowserView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "cardbrowser")
    CardBrowserPresenter mCardBrowserPresenter;

    @BindView(R.id.rv_cards) RecyclerView rvCards;

    private CardsAdapter mCardsAdapter;

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

    }

    @Override
    public void detachInputListeners() {

    }

    @Override
    public void replaceCardsRecyclerData(ArrayList<Card> cards) {
        mCardsAdapter.replaceData(cards);
    }

    @Override
    public void updateCardsRecyclerData(ArrayList<Card> cards) {
        mCardsAdapter.notifyItemInserted(cards.size() - 1);
    }
}
