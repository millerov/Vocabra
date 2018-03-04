package com.example.alexmelnikov.vocabra.ui.translator;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.adapter.LanguageAdapter;
import com.example.alexmelnikov.vocabra.api.ApiHelper;
import com.example.alexmelnikov.vocabra.api.ApiService;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.api.TranslationResult;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainPresenter;
import com.example.alexmelnikov.vocabra.utils.Constants;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;
import com.example.alexmelnikov.vocabra.utils.TextUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends BaseFragment implements TranslatorView {

    @InjectPresenter
    TranslatorPresenter mTranslatorPresenter;

    @BindView(R.id.ed_translate)
    EditText etTranslate;
    @BindView(R.id.tv_translated)
    TextView tvTranslated;
    @BindView(R.id.btn_clear)
    ImageButton btnClear;
    @BindView(R.id.spin_from)
    Spinner mSpinFrom;
    @BindView(R.id.spin_to)
    Spinner mSpinTo;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_swap)
    ImageButton btnSwap;
    @BindView(R.id.tv_langtag)
    TextView tvLangTag;
    @BindView(R.id.btn_favourite)
    ImageButton btnFavoutite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translator, container, false);
        ButterKnife.bind(this, view);

        tvTranslated.setMovementMethod(new ScrollingMovementMethod());

        tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        tvMessage.setClickable(true);
        tvMessage.setText(Html.fromHtml(getString(R.string.inf_yandex_translate_api)));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setupSpinners(ArrayList<Language> langList, int from, int to) {
        if (mSpinFrom.getAdapter() == null || mSpinTo.getAdapter() == null) {
            Collections.sort(langList);
            LanguageAdapter spinAdapter = new LanguageAdapter(getActivity(), langList);
            mSpinFrom.setAdapter(spinAdapter);
            mSpinTo.setAdapter(spinAdapter);

            mSpinFrom.setSelection(from);
            mSpinTo.setSelection(to);
        }

    }

    @Override
    public void attachInputListeners() {
        Disposable spinnerFrom = RxAdapterView.itemSelections(mSpinFrom)
                .skip(1)
                .subscribe(index -> mTranslatorPresenter.selectorFrom(index));

        Disposable spinnerTo = RxAdapterView.itemSelections(mSpinTo)
                .skip(1)
                .subscribe(index -> mTranslatorPresenter.selectorTo(index));

        Disposable clearInputButton = RxView.clicks(btnClear)
                .subscribe(o -> mTranslatorPresenter.clearInputPressed());

        Disposable inputChanges = RxTextView.textChanges(etTranslate)
                 .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                 .map(charSequence -> charSequence.toString())
                 .filter(text -> !text.isEmpty())
                 .subscribe(text -> {
                     Log.d("MyTag", "Text:" + text);
                     mTranslatorPresenter.inputChanges(text);
                     mTranslatorPresenter.translationRequested(text, mSpinFrom.getSelectedItem().toString(), mSpinTo.getSelectedItem().toString());
                     Log.d("MyTag", "Text went to translate");
                 });

        mDisposable.addAll(inputChanges, spinnerFrom, spinnerTo, clearInputButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void showTranslationResult(String result) {
        tvTranslated.setText(result);
        tvTranslated.setVisibility(View.VISIBLE);
    }

    @Override
    public void changeLanguagesSelected(int from, int to) {
        mSpinFrom.setSelection(from);
        mSpinTo.setSelection(to);
    }

    @Override
    public void fillTextFields(String from, String translated, String to) {
        etTranslate.setText(from);
        tvTranslated.setText(translated);
        tvLangTag.setText(to);
    }

    @Override
    public void showMessage() {
        tvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMessage() {
        tvMessage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideResults() {
        tvTranslated.setVisibility(View.GONE);
    }
}

