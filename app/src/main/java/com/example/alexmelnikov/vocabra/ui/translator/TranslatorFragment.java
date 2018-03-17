package com.example.alexmelnikov.vocabra.ui.translator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.DecksSpinnerAdapter;
import com.example.alexmelnikov.vocabra.adapter.HistoryAdapter;
import com.example.alexmelnikov.vocabra.adapter.LanguageAdapter;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.example.alexmelnikov.vocabra.ui.translation.TranslationFragment;
import com.example.alexmelnikov.vocabra.utils.TextUtils;
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
    TextInputLayout mDialogTilFront;
    TextInputLayout mDialogTilBack;
    TextInputLayout mDialogTilContext;

    private HistoryAdapter mHistoryAdapter;
    private int adapterAnimDelay;


    public static TranslatorFragment newInstance(@Nullable Translation translation, boolean fromTranslationFragment) {
        Bundle args = new Bundle();
        args.putSerializable("translation", translation);
        args.putSerializable("fromTranslationFragment", fromTranslationFragment);
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

        // Setting delay so transition animation from transitionFragment wouldn't conflict with recyclerview animation
        adapterAnimDelay = getArguments().getSerializable("fromTranslationFragment").toString().equals("false") ? 70 : 410;

        mTranslatorPresenter.setInputOutput((Translation) getArguments().getSerializable("translation"));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHistoryAdapter = new HistoryAdapter(getActivity(), new ArrayList<Translation>(), mTranslatorPresenter);
        RecyclerView.LayoutManager layman= new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvHistory.setLayoutManager(layman);
                rvHistory.setAdapter(mHistoryAdapter);
                rvHistory.scheduleLayoutAnimation();
                rvHistory.invalidate();
            }
        }, adapterAnimDelay);

        etTranslate.setFocusable(false);
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
    public void attachInputListeners() {
        Disposable spinnerFrom = RxAdapterView.itemSelections(mSpinFrom)
                .skip(2)
                .subscribe(index -> mTranslatorPresenter.selectorFrom(index));

        Disposable spinnerTo = RxAdapterView.itemSelections(mSpinTo)
                .skip(2)
                .subscribe(index -> mTranslatorPresenter.selectorTo(index));

        Disposable clearInputButton = RxView.clicks(btnClear)
                .subscribe(o -> mTranslatorPresenter.clearButtonPressed());

        Disposable inputTouched = RxView.clicks(etTranslate)
                .subscribe(o -> mTranslatorPresenter.inputRequested());

        Disposable swapButton = RxView.clicks(btnSwap)
                .subscribe(o -> mTranslatorPresenter.swapSelection());

        Disposable copyButton = RxView.clicks(btnCopy)
                .subscribe(o -> mTranslatorPresenter.copyButtonPressed());

        mDisposable.addAll(inputTouched, spinnerFrom, spinnerTo, clearInputButton,
                swapButton, copyButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void openTranslationFragment(String fromText, String toText, String fromLang, String toLang) {
        TranslationFragment fragment = TranslationFragment.newInstance(fromText, toText, fromLang, toLang);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(370);

            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

            fragment.setEnterTransition(new AutoTransition());
            fragment.setSharedElementEnterTransition(changeBoundsTransition);
            fragment.setSharedElementReturnTransition(changeBoundsTransition);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                //  .addToBackStack(null)
                // .addSharedElement(btnClear, "transition")
                .addSharedElement(rlTranslator, "viewtrans")
                .commit();

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
        //Snackbar.make(getView(), "Перевод скопирован", Snackbar.LENGTH_SHORT).show();
        ((MainActivity)getActivity()).showMessage("Перевод скопирован", false, mTranslatorPresenter);

    }


    @Override
    public void showFavoriteDropMessage() {
        ((MainActivity)getActivity()).showMessage("Перевод удален из вашей коллекции",
                true, mTranslatorPresenter);
    }

    @Override
    public void showAddCardDialog(int pos, Translation translation, ArrayList<Deck> decks) {
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Добавление карточки")
                        .customView(R.layout.dialog_add_card, true)
                        .positiveText("Добавить")
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog1, which) -> mTranslatorPresenter.addNewCardFromHistoryResultPassed(pos,
                                translation, etDialogFront.getText().toString(), etDialogBack.getText().toString(),
                                etDialogContext.getText().toString(), (String) mDialogSpinDecks.getSelectedItem(),
                                getResources().getColor(R.color.colorPrimary)))
                        .build();

        etDialogFront = (EditText) dialog.getView().findViewById(R.id.et_front);
        etDialogBack = (EditText) dialog.getView().findViewById(R.id.et_back);
        etDialogContext = (EditText) dialog.getView().findViewById(R.id.et_context);
        mDialogSpinDecks = (Spinner) dialog.getView().findViewById(R.id.spin_decks);
        mDialogTilFront = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_front);
        mDialogTilBack = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_back);
        mDialogTilContext = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_context);

        etDialogFront.setText(translation.getFromText());
        etDialogBack.setText(translation.getToText());
        etDialogFront.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogBack.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogContext.setInputType(InputType.TYPE_CLASS_TEXT);
        mDialogTilFront.setHint("Передняя сторона " + TextUtils.getFirstLanguageIndexFromDir(translation.getLangs()));
        mDialogTilBack.setHint("Задняя сторона " + TextUtils.getSecondLanguageIndexFromDir(translation.getLangs()));
        mDialogTilContext.setError("Контекст поможет новому слову лучше отложиться в памяти");
        etDialogContext.requestFocus();
        etDialogFront.setSelection(etDialogFront.getText().length());
        mDialogSpinDecks.setAdapter(new DecksSpinnerAdapter(getActivity(), decks));
        dialog.show();
    }

}

