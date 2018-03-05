package com.example.alexmelnikov.vocabra.ui.translator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.HistoryAdapter;
import com.example.alexmelnikov.vocabra.adapter.LanguageAdapter;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends BaseFragment implements TranslatorView {

    private static final String TAG = "TranslatorFragment";
    
    @InjectPresenter(type = PresenterType.GLOBAL, tag = "translator")
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
    @BindView(R.id.tv_langtagto)
    TextView tvLangTagTo;
    @BindView(R.id.btn_favourite)
    ImageButton btnFavoutite;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R.id.tv_langtagfrom)
    TextView tvLangTagFrom;

    private HistoryAdapter mHistoryAdapter;

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
        Log.d("Adapter", "adaper creation");
        mHistoryAdapter = new HistoryAdapter(getActivity(), new ArrayList<Translation>());
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvHistory.setAdapter(mHistoryAdapter);
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
                     mTranslatorPresenter.translationRequested(text);
                     Log.d("MyTag", "Text went to translate");
                 });

        Disposable swapButton = RxView.clicks(btnSwap)
                .subscribe(o -> mTranslatorPresenter.swapSelection());

        mDisposable.addAll(inputChanges, spinnerFrom, spinnerTo, clearInputButton, swapButton);
    }


    @Override
    public void replaceData(ArrayList<Translation> translations) {
        mHistoryAdapter.replaceData(translations);
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
    public void fillTextFields(String input, String translated, String fromLang, String toLang) {
        etTranslate.setText(input);
        tvTranslated.setText(translated);
        tvLangTagFrom.setText(fromLang);
        tvLangTagTo.setText(toLang);
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

