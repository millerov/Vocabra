package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Fade;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.example.alexmelnikov.vocabra.ui.deck_add.DeckAddFragment;
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
    @BindView(R.id.fab_add) FloatingActionButton btnAddCard;
    @BindView(R.id.layout_toolbar) LinearLayout layoutToolbar;

    @BindView(R.id.layout_deck_cards) LinearLayout layoutDeckCards;
    @BindView(R.id.rl_deck) RelativeLayout rlDeck;
    @BindView(R.id.tv_deck_name) TextView tvDeckName;


    private CardsAdapter mCardsAdapter;
    private RecyclerView rvDecks;
    private MaterialDialog decksDialog;

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
        mCardsAdapter = new CardsAdapter(getActivity(), new ArrayList<Card>(), mCardBrowserPresenter);
        mCardsAdapter.setHasStableIds(true);

        rvCards.setLayoutManager(new CardsLinearLayoutManager(getActivity()));
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
    public void showDecksListDialog(ArrayList<Deck> decks) {
        decksDialog = new MaterialDialog.Builder(getActivity())
                .title("Мои колоды")
                .customView(R.layout.dialog_decks, false)
                .positiveText("Добавить колоду")
                .negativeText("Назад")
                .onPositive(((dialog1, which) -> mCardBrowserPresenter.createNewDeckRequest()))
                .build();

        Log.d(TAG, "showDecksListDialog: " + decks.size());
        rvDecks = decksDialog.getView().findViewById(R.id.rv_decks);
        rvDecks.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDecks.setAdapter(new DecksDialogAdapter(getActivity(), decks, mCardBrowserPresenter));

        decksDialog.show();
    }

    @Override
    public void hideDecksListDialog() {
        decksDialog.hide();
    }


    @Override
    public void openDeckCreationFragment() {
        DeckAddFragment fragment = new DeckAddFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(370);

            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

            fragment.setEnterTransition(new Slide().setDuration(370));
         //   fragment.setSharedElementEnterTransition(changeBoundsTransition);
            fragment.setSharedElementReturnTransition(changeBoundsTransition);
        }

        Log.d(TAG, "openDeckCreationFragment: " + this.getClass().getName());

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .addSharedElement(btnAddCard, "fabAdd")
                .commitAllowingStateLoss();
    }

    @Override
    public void showDeckCardview(Deck deck) {
        rlDeck.setVisibility(View.VISIBLE);
        tvDeckName.setText(deck.getName());

        final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
        rlDeck.setBackground(drawable);

        layoutDeckCards.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutDeckCards.animate()
                        .y(layoutToolbar.getHeight())
                        .setDuration(200)
                        .start();
            }
        }, 400);


    }

    @Override
    public void hideDeckCardview() {
        layoutDeckCards.animate()
                .y(0)
                .setDuration(200)
                .start();
    }

    public void changeDeckButtonSrc(boolean showingDeckCards) {
        if (showingDeckCards)
            btnDecks.setImageResource(R.drawable.ic_arrow_back_white_24dp);
        else
            btnDecks.setImageResource(R.drawable.ic_filter_none_white_24dp);
    }

}
