package com.example.alexmelnikov.vocabra.ui.translator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.adapter.DecksSpinnerAdapter;
import com.example.alexmelnikov.vocabra.adapter.HistoryAdapter;
import com.example.alexmelnikov.vocabra.adapter.HistoryRecyclerItemTouchHelper;
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
 * TranslatorFragment.java – translator fragment class
 * @author Alexander Melnikov
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
    @BindView(R.id.btn_favourite) ImageButton btnFavourite;
    @BindView(R.id.rv_history) RecyclerView rvHistory;
    @BindView(R.id.tv_langtagfrom) TextView tvLangTagFrom;
    @BindView(R.id.layout_translated) RelativeLayout transitionsContainer;
    @BindView(R.id.layout_translator) RelativeLayout rlTranslator;
    @BindView(R.id.toolbar_layout) RelativeLayout rlToolbar;
    @BindView(R.id.sv_mainscroll) ScrollView svTranslationAndHistory;
    @BindView(R.id.scroll_container) LinearLayout scrollContainer;
    @BindView(R.id.layout_history_container) RelativeLayout historyContainer;
    @BindView(R.id.layout_empty_history_msg) LinearLayout emptyHistoryMessage;


    //addCardDialog views
    EditText etDialogFront;
    EditText etDialogBack;
    EditText etDialogContext;
    Spinner mDialogSpinDecks;
    TextInputLayout mDialogTilFront;
    TextInputLayout mDialogTilBack;
    TextInputLayout mDialogTilContext;

    private HistoryAdapter mHistoryAdapter;
    private int adapterAnimDelay;


    public static TranslatorFragment newInstance(@Nullable Translation translation, boolean fromTranslationFragment, boolean translationNotNull) {
        Bundle args = new Bundle();
        args.putSerializable("translation", translation);
        args.putBoolean("fromTranslationFragment", fromTranslationFragment);
        args.putBoolean("translationNotNull", translationNotNull);
        TranslatorFragment fragment = new TranslatorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translator, container, false);
        ButterKnife.bind(this, view);

        tvTranslated.setMovementMethod(new ScrollingMovementMethod());

        /*Setting delay before animating the history recycler view to avoid friction between it, transition animation and animation of the
          translation card appearance */
        boolean fromTranslationFragment = getArguments().getBoolean("fromTranslationFragment");
        boolean translationNotNull = getArguments().getBoolean("translationNotNull");

        if (!fromTranslationFragment) {
            adapterAnimDelay = 0;
        } else {
            if (!translationNotNull) {
                adapterAnimDelay = 410;
            } else {
                adapterAnimDelay = 850;
            }
        }

        mTranslatorPresenter.setInputOutput((Translation) getArguments().getSerializable("translation"));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHistoryAdapter = new HistoryAdapter(getActivity(), new ArrayList<>(), mTranslatorPresenter);
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

                ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new HistoryRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT , mTranslatorPresenter);
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvHistory);
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

        Disposable addFavouriteButton = RxView.clicks(btnFavourite)
                .subscribe(o -> mTranslatorPresenter.translationCardFavButtonPressed());

        Disposable inputTouched = RxView.clicks(etTranslate)
                .subscribe(o -> mTranslatorPresenter.inputRequested());

        Disposable swapButton = RxView.clicks(btnSwap)
                .subscribe(o -> mTranslatorPresenter.swapSelection());

        Disposable copyButton = RxView.clicks(btnCopy)
                .subscribe(o -> mTranslatorPresenter.copyButtonPressed());

        mDisposable.addAll(inputTouched, spinnerFrom, spinnerTo, clearInputButton,
                addFavouriteButton, swapButton, copyButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void openTranslationFragment(String fromText, String toText, String fromLang, String toLang) {
        TranslationFragment fragment = TranslationFragment.newInstance(fromText, toText, fromLang, toLang);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideBottomNavigationBar();
        }

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
                    .addToBackStack(null)
                    // .addSharedElement(btnClear, "transition")
                    .addSharedElement(rlTranslator, "viewtrans")
                    .commit();


    }

    @Override
    public void replaceHistoryData(ArrayList<Translation> translations) {
        if (translations.isEmpty())
            emptyHistoryMessage.setVisibility(View.VISIBLE);
        else if (emptyHistoryMessage.getVisibility() == View.VISIBLE)
            emptyHistoryMessage.setVisibility(View.GONE);

        mHistoryAdapter.replaceData(translations);
    }


    @Override
    public void updateHistoryDataElement(int pos, Translation translation) {
        mHistoryAdapter.updateElement(pos, translation);
    }

    @Override
    public void showTranslationResult(String result) {
        tvTranslated.setText(result);
        tvTranslated.setVisibility(View.VISIBLE);
        btnCopy.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);
        btnFavourite.setVisibility(View.VISIBLE);
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
            btnFavourite.setVisibility(View.VISIBLE);
        }
        tvLangTagFrom.setText(fromLang);
        tvLangTagTo.setText(toLang);
    }

    @Override
    public void changeFavouriteButtonAppearance(boolean light) {
        if (light)
            btnFavourite.setImageResource(R.drawable.ic_star_yellow_24dp);
        else
            btnFavourite.setImageResource(R.drawable.ic_star_border_white_24dp);
    }

    @Override
    public void clearInputOutput() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_clear_black_anim_24dp);
        btnClear.setImageDrawable(drawable);
        drawable.start();
        etTranslate.setText("");
        tvTranslated.setText("");
        btnCopy.setVisibility(View.INVISIBLE);
        btnFavourite.setVisibility(View.GONE);

        btnClear.postDelayed(() -> btnClear.setVisibility(View.INVISIBLE), 520);

    }

    @Override
    public void copyAction(String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("output", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
        ((MainActivity)getActivity()).showMessage(0, "Перевод скопирован", false, null, null);

    }


    @Override
    public void showFavoriteDropMessage() {
        ((MainActivity)getActivity()).showMessage(1, "Перевод удален из вашей коллекции",
                true, mTranslatorPresenter, "Отменить");
    }

    @Override
    public void showAddCardDialog(int pos, Translation translation, ArrayList<Deck> decks) {
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Добавление карточки")
                        .customView(R.layout.dialog_add_card, true)
                        .positiveText("Добавить")
                        .negativeText(android.R.string.cancel)
                        .autoDismiss(false)
                        .onNegative(((dialog1, which) -> dialog1.dismiss()))
                        .onPositive((dialog1, which) -> {
                            if (etDialogFront.getText().toString().trim().isEmpty())
                                mDialogTilFront.setError("Введите слово или фразу");
                            if (etDialogBack.getText().toString().trim().isEmpty())
                                mDialogTilBack.setError("Введите слово или фразу");
                            if (!etDialogFront.getText().toString().trim().isEmpty() &&
                                    !etDialogBack.getText().toString().trim().isEmpty()) {
                                mTranslatorPresenter.addNewCardFromHistoryResultPassed(pos,
                                        translation, etDialogFront.getText().toString(), etDialogBack.getText().toString(),
                                        etDialogContext.getText().toString(), (String) mDialogSpinDecks.getSelectedItem(),
                                        getResources().getColor(R.color.colorPrimary));
                                dialog1.dismiss();
                            }
                        })
                        .build();

        etDialogFront = dialog.getView().findViewById(R.id.et_front);
        etDialogBack = dialog.getView().findViewById(R.id.et_back);
        etDialogContext = dialog.getView().findViewById(R.id.et_context);
        mDialogSpinDecks = dialog.getView().findViewById(R.id.spin_decks);
        mDialogTilFront = dialog.getView().findViewById(R.id.input_layout_front);
        mDialogTilBack = dialog.getView().findViewById(R.id.input_layout_back);
        mDialogTilContext = dialog.getView().findViewById(R.id.input_layout_context);

        etDialogFront.setText(translation.getFromText());
        etDialogBack.setText(translation.getToText());
        etDialogFront.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogBack.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogContext.setInputType(InputType.TYPE_CLASS_TEXT);
        mDialogTilFront.setHint("Передняя сторона " + TextUtils.getFirstLanguageIndexFromDir(translation.getLangs()));
        mDialogTilBack.setHint("Задняя сторона " + TextUtils.getSecondLanguageIndexFromDir(translation.getLangs()));
        mDialogTilContext.setError("Контекст поможет новому слову лучше отложиться в памяти");
        etDialogContext.requestFocus();
        mDialogSpinDecks.setAdapter(new DecksSpinnerAdapter(getActivity(), decks, true));
        dialog.show();
    }


    @Override
    public void showDeleteOptionsDialog(int pos) {
        new MaterialDialog.Builder(getActivity())
                .items(R.array.delete_options)
                .itemsCallback((dialog, view, which, text) -> mTranslatorPresenter.deleteDialogOptionPicked(pos, which))
                .show();
    }


    @Override
    public void showItemDeletedFromHistoryMessage() {
        ((MainActivity)getActivity()).showMessage(2, "Перевод удален из истории", true, mTranslatorPresenter, "Отменить");
    }

    @Override
    public void showHistoryCleanedMessage() {
        ((MainActivity)getActivity()).showMessage(3, "История отчищена", true, mTranslatorPresenter, "Отменить");
    }

    @Override
    public void showTranslationCard() {
        svTranslationAndHistory.postDelayed(() -> {
            transitionsContainer.setVisibility(View.VISIBLE);
            scrollContainer.animate()
                    .y(rlToolbar.getHeight() + rlTranslator.getHeight())
                    .setDuration(200)
                    .withEndAction(() -> {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        lp.setMargins(0,0,0, transitionsContainer.getHeight() + 80);
                        historyContainer.setLayoutParams(lp);
                        historyContainer.requestLayout();
                    })
                    .start();
        }, 550);

    }

    @Override
    public void hideTranslationCard() {
        scrollContainer.animate()
                .y(0)
                .setDuration(240)
                .start();
        svTranslationAndHistory.postDelayed(() -> {
            transitionsContainer.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lp.setMargins(0,0,0, 0);
            historyContainer.setLayoutParams(lp);
            historyContainer.requestLayout();
        }, 260);

    }

    //Without this override while translated card view is on screen back press screws the whole layout over
    @Override
    public boolean onBackPressed() {
        getActivity().finish();
        return super.onBackPressed();
    }
}


