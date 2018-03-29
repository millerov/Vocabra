package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.DecksForTrainingAdapter;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.deck_add.DeckAddFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.example.alexmelnikov.vocabra.ui.training.TrainingFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

public class DecksForTrainingFragment extends BaseFragment implements DecksForTrainingView {

    private static final String TAG = "MyTag";

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
        mDecksAdapter = new DecksForTrainingAdapter(getActivity(), new ArrayList<Deck>(), mDecksForTrainPresenter);
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

    @Override
    public void openTrainingActivity(Deck deck, View item, String transitionName) {
        ((MainActivity)getActivity()).hideBottomNavigationBar();

        TrainingFragment fragment = new TrainingFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(370);

            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

            fragment.setEnterTransition(new AutoTransition().setDuration(370));
            fragment.setSharedElementEnterTransition(changeBoundsTransition);
            fragment.setSharedElementReturnTransition(changeBoundsTransition);
        }

        Bundle bundle = new Bundle();
        bundle.putString("transitionName", transitionName);
        bundle.putSerializable("deck", deck);
        fragment.setArguments(bundle);


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addSharedElement(item, transitionName)
                .addToBackStack(null)
                .commit();

                //.addSharedElement(btnAddCard, "fabAdd")
                //.commitAllowingStateLoss();
    }
}
