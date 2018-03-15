package com.example.alexmelnikov.vocabra.ui.deck_add;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.LanguageAdapter;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserFragment;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * Created by AlexMelnikov on 15.03.18.
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

    private int lastDialogColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck_add, container, false);
        ButterKnife.bind(this, view);

        lastDialogColor = getResources().getColor(R.color.colorPrimary);
        mDeckAddPresenter.updateSelectedColor(lastDialogColor);

        etDeckName.setInputType(InputType.TYPE_CLASS_TEXT);
        etDeckName.requestFocus();
        etDeckName.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etDeckName, 0);
            }
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

        mDisposable.addAll(changeColorButton, spinnerFrom, spinnerTo, swapButton, confirmButton);
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
    public void changeLanguagesSelected(int from, int to) {
        mSpinFrom.setSelection(from);
        mSpinTo.setSelection(to);
    }

    @Override
    public void closeFragment() {
        View view = getView();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStackImmediate();
            }
        }, 300);


    }

    @Override
    public void showEditTextError(String message) {
        etDeckName.setError(message);
    }

    @Override
    public void showSelectColorDialog() {
        TypedArray colors = getResources().obtainTypedArray(R.array.custom_colors);
        int[] colorsInts = new int[colors.length()];
        for (int i = 0; i < colors.length(); i++) {
            colorsInts[i] = colors.getColor(i,0);
        }

        new SpectrumDialog.Builder((getContext()))
                .setColors(R.array.custom_colors)
                .setDismissOnColorSelected(true)
                .setSelectedColor(lastDialogColor)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, int color) {
                        colorChanged(color);
                        lastDialogColor = color;
                        mDeckAddPresenter.updateSelectedColor(lastDialogColor);
                    }
                }).build().show(getFragmentManager(), "color_dialog");

    }

    public void colorChanged(int color) {
        final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        rvDeck.setBackground(drawable);
    }

}