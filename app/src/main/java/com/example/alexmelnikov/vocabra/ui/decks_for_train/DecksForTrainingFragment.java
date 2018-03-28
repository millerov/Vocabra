package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.DecksForTrainingAdapter;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

public class DecksForTrainingFragment extends BaseFragment implements DecksForTrainingView {

    @InjectPresenter
    DecksForTrainingPresenter mDecksForTrainPresenter;

    @BindView(R.id.layout_toolbar) RelativeLayout rlToolBar;
    @BindView(R.id.rv_decks) RecyclerView rvDecks;

    private DecksForTrainingAdapter mDecksAdapter;
    private DecksLinearLayoutManager mDecksRvManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decks_for_training, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDecksAdapter = new DecksForTrainingAdapter(getActivity(), new ArrayList<Deck>());
        mDecksAdapter.setHasStableIds(true);

        mDecksRvManager = new DecksLinearLayoutManager(getActivity());
        rvDecks.setLayoutManager(mDecksRvManager);
        rvDecks.setAdapter(mDecksAdapter);
    }

    @Override
    public void attachInputListeners() {

    }

    @Override
    public void detachInputListeners() {

    }

    @Override
    public void replaceCardsRecyclerData(ArrayList<Deck> decks) {
        mDecksAdapter.replaceData(decks);
    }
}
