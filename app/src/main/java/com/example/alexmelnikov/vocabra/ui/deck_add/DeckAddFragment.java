package com.example.alexmelnikov.vocabra.ui.deck_add;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.LanguageAdapter;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * DeckAddFragment.java – fragment for adding new decks
 * @author Alexander Melnikov
 */

public class DeckAddFragment extends BaseFragment implements DeckAddView {

    private static final String TAG = "MyTag";

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "deckadd")
    DeckAddPresenter mDeckAddPresenter;

    @BindView(R.id.spin_from) Spinner mSpinFrom;
    @BindView(R.id.spin_to) Spinner mSpinTo;
    @BindView(R.id.et_deck_name) EditText etDeckName;
    @BindView(R.id.btn_swap) ImageButton btnSwap;
    @BindView(R.id.fab_add) FloatingActionButton btnConfirm;
    @BindView(R.id.btn_change_color) ImageButton btnChangeColor;
    @BindView(R.id.rv_deck) RelativeLayout rvDeck;
    @BindView(R.id.input_layout_deck_name) TextInputLayout tilDeckName;
    @BindView(R.id.tv_heading) TextView tvDeckName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck_add, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).hideBottomNavigationBar();

        etDeckName.setInputType(InputType.TYPE_CLASS_TEXT);
        etDeckName.requestFocus();
        etDeckName.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etDeckName, 0);
        }, 150);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void attachInputListeners() {
        Disposable changeColorButton = RxView.clicks(btnChangeColor)
                .subscribe(o -> mDeckAddPresenter.colorChangeButtonPressed());

        Disposable spinnerFrom = RxAdapterView.itemSelections(mSpinFrom)
                .skip(1)
                .subscribe(index -> mDeckAddPresenter.selectorFrom(index));

        Disposable spinnerTo = RxAdapterView.itemSelections(mSpinTo)
                .skip(1)
                .subscribe(index -> mDeckAddPresenter.selectorTo(index));

        Disposable swapButton = RxView.clicks(btnSwap)
                .subscribe(o -> mDeckAddPresenter.swapSelection());

        Disposable confirmButton = RxView.clicks(btnConfirm)
                .subscribe(o -> mDeckAddPresenter.addNewDeckRequest(etDeckName.getText().toString()));

        Disposable inputChanges = RxTextView.textChanges(etDeckName)
                .debounce(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString())
                .filter(text -> !text.isEmpty())
                .subscribe(text -> {
                    mDeckAddPresenter.inputChanges(text);
                });


        mDisposable.addAll(changeColorButton, spinnerFrom, spinnerTo, swapButton, confirmButton, inputChanges);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void setupSpinners(ArrayList<Language> languages, int from, int to) {
        if (mSpinFrom.getAdapter() == null || mSpinTo.getAdapter() == null) {
            Collections.sort(languages);
            LanguageAdapter spinAdapter = new LanguageAdapter(getActivity(), languages);
            mSpinFrom.setAdapter(spinAdapter);
            mSpinTo.setAdapter(spinAdapter);

            mSpinFrom.setSelection(from);
            mSpinTo.setSelection(to);
        }
    }

    @Override
    public void setupDefaultColor() {
        mDeckAddPresenter.updateSelectedColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void updateCardColor(int color) {
        final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        rvDeck.setBackground(drawable);
    }

    @Override
    public void changeLanguagesSelected(int from, int to) {
        mSpinFrom.setSelection(from);
        mSpinTo.setSelection(to);
    }

    @Override
    public void closeFragment(int createdDeckId) {

        ((MainActivity)getActivity()).deckCreated(createdDeckId);
        ((MainActivity)getActivity()).showMessage(0, "Колода добавлена", false, mDeckAddPresenter, null);

        View view = getView();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        ((MainActivity) getActivity()).showBottomNavigationBar();

        view.postDelayed(() -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
        }, 300);


    }

    @Override
    public void hideNameEditTextError() {
        tilDeckName.setErrorEnabled(false);
    }

    @Override
    public void showNameEditTextError(String message) {
        tilDeckName.setErrorEnabled(true);
        tilDeckName.setError(message);
    }

    @Override
    public void showSelectColorDialog() {
        new SpectrumDialog.Builder((getContext()))
                .setTitle("Цвет колоды")
                .setColors(R.array.custom_colors)
                .setDismissOnColorSelected(true)
                .setSelectedColor(DeckAddPresenter.selectedColor)
                .setOnColorSelectedListener((positiveResult, color) -> mDeckAddPresenter.updateSelectedColor(color))
                .build()
                .show(getFragmentManager(), "color_dialog");

    }

    @Override
    public boolean onBackPressed() {
        ((MainActivity)getActivity()).showBottomNavigationBar();
        return super.onBackPressed();
    }

}
