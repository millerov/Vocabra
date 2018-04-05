package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.adapter.DecksForTrainingAdapter;
import com.example.alexmelnikov.vocabra.adapter.layout_manager.DecksLinearLayoutManager;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.example.alexmelnikov.vocabra.ui.training.TrainingFragment;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

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
    @BindView(R.id.tv_cards_ready_counter)
    TextView tvCardsReadyCounter;

    private DecksForTrainingAdapter mDecksAdapter;
    private DecksLinearLayoutManager mDecksRvManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        VocabraApp.getAppComponent().inject(this);
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

    public void setupTextView(int counter) {
        tvCardsReadyCounter.setText("На сегодня " + counter + " " + TextUtils.getRightWordEnding(counter, new String[]{"карточка", "карточки", "карточек"}));
    }

    @Override
    public void showEmptyDeckSelectedMessage(View item) {
        ((MainActivity)getActivity()).showMessage(0, "В колоде пока нет карточек для тренеровки", false, null, null);
        YoYo.with(Techniques.Bounce)
                .duration(600)
                .playOn(item);
    }

    @Override
    public void replaceCardsRecyclerData(ArrayList<Deck> decks) {
        mDecksAdapter.replaceData(decks);
    }

    @Override
    public void openTrainingFragment(Deck deck, View item, String transitionName) {
        ((MainActivity)getActivity()).hideBottomNavigationBar();

        TrainingFragment fragment = new TrainingFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.default_transition));
            fragment.setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.default_transition));
        }

        Bundle bundle = new Bundle();
        bundle.putString("transitionName", transitionName);
        bundle.putInt("deckId", deck.getId());
        fragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addSharedElement(item, transitionName)
                .addToBackStack(null)
                .commit();
    }
}
