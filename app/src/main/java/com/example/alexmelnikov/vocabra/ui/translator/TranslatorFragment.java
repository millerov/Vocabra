package com.example.alexmelnikov.vocabra.ui.translator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;


/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends BaseFragment implements TranslatorView {

    private static final String TAG = "MyTag";
    
    @InjectPresenter(type = PresenterType.GLOBAL, tag = "translator")
    TranslatorPresenter mTranslatorPresenter;

    @BindView(R.id.ed_translate) EditText etTranslate;
    @BindView(R.id.tv_translated) TextView tvTranslated;
    @BindView(R.id.btn_clear) ImageButton btnClear;
    @BindView(R.id.btn_copy) ImageButton btnCopy;
    @BindView(R.id.spin_from) Spinner mSpinFrom;
    @BindView(R.id.spin_to) Spinner mSpinTo;
    @BindView(R.id.btn_swap) ImageButton btnSwap;
    @BindView(R.id.tv_langtagto) TextView tvLangTagTo;
    @BindView(R.id.btn_favourite) ImageButton btnFavoutite;
    @BindView(R.id.rv_history) RecyclerView rvHistory;
    @BindView(R.id.tv_langtagfrom) TextView tvLangTagFrom;
    @BindView(R.id.layout_translated) RelativeLayout transitionsContainer;
    @BindView(R.id.layout_translator) RelativeLayout rlTranslator;

    EditText etDialogFront;
    EditText etDialogBack;
    EditText etDialogContext;
    Spinner mDialogSpinDecks;

    private HistoryAdapter mHistoryAdapter;

    public static TranslatorFragment newInstance(Translation translation) {
        Bundle args = new Bundle();
        args.putSerializable("translation", translation);
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
        mTranslatorPresenter.setInputOutput((Translation) getArguments().getSerializable("translation"));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHistoryAdapter = new HistoryAdapter(getActivity(), new ArrayList<Translation>(), mTranslatorPresenter);
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
                .subscribe(o -> mTranslatorPresenter.clearButtonPressed());

        Disposable inputTouched = RxView.clicks(etTranslate)
                .subscribe(o -> mTranslatorPresenter.inputRequested());

        Disposable swapButton = RxView.clicks(btnSwap)
                .subscribe(o -> mTranslatorPresenter.swapSelection());

        Disposable copyButton = RxView.clicks(btnCopy)
                .subscribe(o -> mTranslatorPresenter.copyButtonPressed());

        mDisposable.addAll(inputTouched, spinnerFrom, spinnerTo, clearInputButton, swapButton);
    }

    @Override
    public void openTranslationFragment(String fromText, String toText, String fromLang, String toLang) {
        TranslationFragment fragment = TranslationFragment.newInstance(fromText, toText, fromLang, toLang);

        ChangeBounds changeBoundsTransition = new ChangeBounds();
        changeBoundsTransition.setDuration(370);

        fragment.setEnterTransition(new AutoTransition());
        fragment.setSharedElementEnterTransition(changeBoundsTransition);
        fragment.setSharedElementReturnTransition(changeBoundsTransition);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
               // .addSharedElement(btnClear, "transition")
                .addSharedElement(rlTranslator, "viewtrans")
                .commit();
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void replaceHistoryData(ArrayList<Translation> translations) {
        mHistoryAdapter.replaceData(translations);
    }

    @Override
    public void updateHistoryDataElement(int pos, Translation translation) {
        mHistoryAdapter.updateElement(pos, translation);
//        mHistoryAdapter.notifyItemChanged(pos, translation);
    }

    @Override
    public void showTranslationResult(String result) {
        tvTranslated.setText(result);
        tvTranslated.setVisibility(View.VISIBLE);
        btnCopy.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);
        btnFavoutite.setVisibility(View.VISIBLE);
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
        if (!translated.isEmpty()) {
            tvTranslated.setText(translated);
            btnCopy.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            btnFavoutite.setVisibility(View.VISIBLE);
        }
        tvLangTagFrom.setText(fromLang);
        tvLangTagTo.setText(toLang);
    }

    @Override
    public void clearInputOutput() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_clear_black_anim_24dp);
        btnClear.setImageDrawable(drawable);
        drawable.start();
        etTranslate.setText("");
        tvTranslated.setText("");
        btnCopy.setVisibility(View.GONE);
        btnFavoutite.setVisibility(View.GONE);
        btnClear.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnClear.setVisibility(View.INVISIBLE);
            }
        }, 400);

    }

    @Override
    public void copyAction(String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("output", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "Перевод скопирован", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAddCardDialog(int pos, Translation translation) {
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Добавление карточки")
                        .customView(R.layout.dialog_add_card, true)
                        .positiveText("Добавить")
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog1, which) -> mTranslatorPresenter
                                .addNewCardFromTranslationResultPassed(pos, translation, etDialogFront.getText().toString(),
                                        etDialogBack.getText().toString(), etDialogContext.getText().toString()))
                        .build();

        etDialogFront = (EditText) dialog.getView().findViewById(R.id.et_front);
        etDialogBack = (EditText) dialog.getView().findViewById(R.id.et_back);
        etDialogContext = (EditText) dialog.getView().findViewById(R.id.et_context);
        mDialogSpinDecks = (Spinner) dialog.getView().findViewById(R.id.spin_decks);
        etDialogFront.setText(translation.getFromText());
        etDialogBack.setText(translation.getToText());
        etDialogFront.requestFocus();
        etDialogFront.setSelection(etDialogFront.getText().length());
        dialog.show();
    }
}

