package com.example.alexmelnikov.vocabra.ui.translator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.alexmelnikov.vocabra.ui.translation.TranslationFragment;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.layout_translated)
    LinearLayout transitionsContainer;
    @BindView(R.id.layout_translator)
    RelativeLayout rlTranslator;

    private HistoryAdapter mHistoryAdapter;

    public static TranslatorFragment newInstance(String fromText, String toText) {
        Bundle args = new Bundle();
        args.putSerializable("fromText", fromText);
        args.putSerializable("toText", toText);
        TranslatorFragment fragment = new TranslatorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translator, container, false);
        ButterKnife.bind(this, view);

        tvTranslated.setMovementMethod(new ScrollingMovementMethod());
        mTranslatorPresenter.setInputOutput(getArguments().getSerializable("fromText").toString(),
                                            getArguments().getSerializable("toText").toString());
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
        etTranslate.setFocusable(false);
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

        Disposable inputTouched = RxView.clicks(etTranslate)
                .subscribe(o -> mTranslatorPresenter.inputRequested());

/*        Disposable inputChanges = RxTextView.textChanges(etTranslate)
                 .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                 .map(charSequence -> charSequence.toString())
                 .filter(text -> !text.isEmpty())
                 .subscribe(text -> {
                     Log.d("MyTag", "Text:" + text);
                     mTranslatorPresenter.inputChanges(text);
                     mTranslatorPresenter.translationRequested(text);
                     Log.d("MyTag", "Text went to translate");
                 });*/

        Disposable swapButton = RxView.clicks(btnSwap)
                .subscribe(o -> mTranslatorPresenter.swapSelection());

        mDisposable.addAll(inputTouched, spinnerFrom, spinnerTo, clearInputButton, swapButton);
    }

    @Override
    public void openTranslationFragment(String fromText, String toText, String fromLang, String toLang) {
        TranslationFragment fragment = TranslationFragment.newInstance(fromText, toText, fromLang, toLang);

        ChangeBounds changeBoundsTransition = new ChangeBounds();
        changeBoundsTransition.setDuration(500);

        fragment.setEnterTransition(new AutoTransition());
        fragment.setSharedElementEnterTransition(changeBoundsTransition);
        fragment.setSharedElementReturnTransition(changeBoundsTransition);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .addSharedElement(btnClear, "transition")
                .addSharedElement(rlTranslator, "viewtrans")
                .commit();
    }

    @Override
    public void replaceHistoryData(ArrayList<Translation> translations) {
        mHistoryAdapter.replaceData(translations);
    }

    @Override
    public void updateHistoryData(ArrayList<Translation> translations) {
        mHistoryAdapter.notifyItemInserted(translations.size() - 1);
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
        if (!input.isEmpty())
            etTranslate.setText(input);
        if (!translated.isEmpty())
            tvTranslated.setText(translated);
        tvLangTagFrom.setText(fromLang);
        tvLangTagTo.setText(toLang);
    }

    @Override
    public void hideResults() {
        tvTranslated.setVisibility(View.GONE);
    }
}

