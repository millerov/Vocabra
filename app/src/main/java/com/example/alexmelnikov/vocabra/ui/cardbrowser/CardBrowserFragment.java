package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.widget.ViewSwitcher;

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
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * Created by AlexMelnikov on 24.02.18.
 */


public class CardBrowserFragment extends BaseFragment implements CardBrowserView {

    private static final String TAG = "MyTag";

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "cardbrowser")
    CardBrowserPresenter mCardBrowserPresenter;

    @BindView(R.id.btn_decks) ImageButton btnDecks;
    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.rv_cards) RecyclerView rvCards;
    @BindView(R.id.fab_add) FloatingActionButton btnAddCard;
    @BindView(R.id.layout_toolbar) LinearLayout layoutToolbar;

    @BindView(R.id.layout_deck_cards) LinearLayout layoutDeckCards;
    @BindView(R.id.rl_deck) RelativeLayout rlDeck;
    @BindView(R.id.tv_deck_name) TextView tvDeckName;
    @BindView(R.id.btn_edit_deck) ImageButton btnEditDeck;
    @BindView(R.id.btn_train) ImageButton btnTrain;
    @BindView(R.id.btn_edit_color) ImageButton btnEditColor;
    @BindView(R.id.btn_confirm) ImageButton btnConfirm;

    @BindView(R.id.et_deck_name)
    EditText etDeckName;


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

        etDeckName.setInputType(InputType.TYPE_CLASS_TEXT);

        rvCards.setLayoutManager(new CardsLinearLayoutManager(getActivity()));
        rvCards.setAdapter(mCardsAdapter);

    }

    @Override
    public void attachInputListeners() {
        Disposable decksButton = RxView.clicks(btnDecks)
                .subscribe(o -> mCardBrowserPresenter.decksButtonPressed());

        Disposable backButton = RxView.clicks(btnBack)
                .subscribe(o -> mCardBrowserPresenter.backButtonPressed());

        Disposable deckNameText = RxView.clicks(tvDeckName)
                .subscribe(o -> mCardBrowserPresenter.editDeckButtonPressed());

        Disposable editDeckButton = RxView.clicks(btnEditDeck)
                .subscribe(o -> mCardBrowserPresenter.editDeckButtonPressed());

        Disposable confirmDeckEdit = RxView.clicks(btnConfirm)
                .subscribe(o -> mCardBrowserPresenter.confirmEditDeckRequest(etDeckName.getText().toString()));

        Disposable editColor = RxView.clicks(btnEditColor)
                .subscribe(o -> mCardBrowserPresenter.editDeckColorRequest());

        mDisposable.addAll(decksButton, editDeckButton, confirmDeckEdit, backButton,
                editColor, deckNameText);
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


        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .addSharedElement(btnAddCard, "fabAdd")
                .commitAllowingStateLoss();
    }

    @Override
    public void showDeckCardview(Deck deck) {
        tvDeckName.setText(deck.getName());
        final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
        rlDeck.setBackground(drawable);

        rlDeck.setVisibility(View.VISIBLE);

    }

    @Override
    public void switchDeckDisplayMode(boolean editModeOn) {
        if (editModeOn) {
            String deckName = tvDeckName.getText().toString();
            etDeckName.setVisibility(View.VISIBLE);
            tvDeckName.setVisibility(View.INVISIBLE);
            etDeckName.setText(deckName);

            etDeckName.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etDeckName, 0);
            etDeckName.setSelection(etDeckName.getText().length());

            btnEditDeck.setVisibility(View.GONE);
            btnTrain.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.VISIBLE);
            btnEditColor.setVisibility(View.VISIBLE);
        } else {
            if (etDeckName.getVisibility() == View.VISIBLE) {
                String deckName = etDeckName.getText().toString();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                etDeckName.setVisibility(View.INVISIBLE);
                tvDeckName.setText(deckName);
                tvDeckName.setVisibility(View.VISIBLE);
                btnEditDeck.setVisibility(View.VISIBLE);
                btnTrain.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.GONE);
                btnEditColor.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void hideDeckCardview() {
        rlDeck.setVisibility(View.GONE);
    }

    @Override
    public void switchCornerButtonState(boolean showingDeckCards) {
        if (showingDeckCards) {
            btnDecks.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);
        }
        else {
            btnBack.setVisibility(View.GONE);
            btnDecks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSelectColorDialog(Deck currentDeck) {
        TypedArray colors = getResources().obtainTypedArray(R.array.custom_colors);
        int[] colorsInts = new int[colors.length()];
        for (int i = 0; i < colors.length(); i++) {
            colorsInts[i] = colors.getColor(i,0);
        }

        new SpectrumDialog.Builder((getContext()))
                .setTitle("Цвет колоды")
                .setColors(R.array.custom_colors)
                .setDismissOnColorSelected(true)
                .setSelectedColor(mCardBrowserPresenter.selectedColor)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, int color) {
                        mCardBrowserPresenter.editDeckColorResultPassed(color);
                    }
                }).build().show(getFragmentManager(), "color_dialog");
    }

    @Override
    public void updateCardColor(int color) {
        final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        rlDeck.setBackground(drawable);
    }

    @Override
    public void showDeckNameEditTextMessage(String message) {
        etDeckName.setError(message);
    }


    @Override
    public boolean onBackPressed() {
        if (etDeckName.getVisibility() == View.VISIBLE ||
                rlDeck.getVisibility() == View.VISIBLE) {
            mCardBrowserPresenter.backButtonPressed();
            return true;
        } else {
            return super.onBackPressed();
        }

    }
}
