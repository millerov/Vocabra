package com.example.alexmelnikov.vocabra.ui.translation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorFragment;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by AlexMelnikov on 06.03.18.
 */

public class TranslationFragment extends BaseFragment implements TranslationView {

    private static final String TAG = "TranslationFragment";

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "translation")
    TranslationPresenter mTranslationPresenter;

    @BindView(R.id.ed_translate)
    EditText etTranslate;
    @BindView(R.id.tv_translated)
    TextView tvTranslated;
    @BindView(R.id.btn_clear)
    ImageButton btnClear;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_translate)
    ImageView btnTranslate;
    @BindView(R.id.layout_translator)
    RelativeLayout rlTranslator;
    @BindView(R.id.rl_to)
    RelativeLayout rlTo;

    public static TranslationFragment newInstance(String fromText, String toText, String fromLang, String toLang) {
        Bundle args = new Bundle();
        args.putSerializable("fromText", fromText);
        args.putSerializable("toText", toText);
        args.putSerializable("fromLang", fromLang);
        args.putSerializable("toLang", toLang);
        TranslationFragment fragment = new TranslationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, view);

        tvTranslated.setMovementMethod(new ScrollingMovementMethod());
        tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        tvMessage.setClickable(true);
        tvMessage.setText(Html.fromHtml(getString(R.string.inf_yandex_translate_api)));

        mTranslationPresenter.setInputOutput(getArguments().getSerializable("fromText").toString(),
                                             getArguments().getSerializable("toText").toString(),
                                             getArguments().getSerializable("fromLang").toString(),
                                             getArguments().getSerializable("toLang").toString());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTranslate.requestFocus();
        etTranslate.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etTranslate, 0);
            }
        }, 150);
    }

    @Override
    public void attachInputListeners() {
        Disposable translateButton = RxView.touches(btnTranslate)
                .subscribe(o -> mTranslationPresenter.continueRequest());

        Disposable translateText = RxView.touches(tvTranslated)
                .subscribe(o -> mTranslationPresenter.continueRequest());

        Disposable clearButton = RxView.clicks(btnClear)
                .subscribe(o -> mTranslationPresenter.clearButtonPressed());

        Disposable inputChanges = RxTextView.textChanges(etTranslate)
                .debounce(150, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString())
                .filter(text -> !text.isEmpty())
                .subscribe(text -> {
                    Log.d("MyTag", "Text:" + text);
                    mTranslationPresenter.inputChanges(text);
                    mTranslationPresenter.translationRequested(text);
                    Log.d("MyTag", "Text went to translate");
                });

        mDisposable.addAll(translateButton, inputChanges, clearButton, translateText);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void fillTextFields(String fromText, String toText, String fromLang, String toLang) {
        etTranslate.setHint("Введите текст (" + fromLang + ")");
        tvTranslated.setHint("Перевод (" + toLang + ")");
        if (!fromText.isEmpty()) {
            etTranslate.setText(fromText);
            tvTranslated.setText(toText);
        }
        etTranslate.setSelection(etTranslate.getText().length());
    }

    @Override
    public void showTranslationResult(String result) {
        tvTranslated.setText(result);
    }

    @Override
    public void clearInputOutput() {
        etTranslate.setText("");
        tvTranslated.setText("");
    }

    @Override
    public boolean onBackPressed() {
        mTranslationPresenter.continueRequest();
        return true;
    }

    @Override
    public void closeFragment(Translation translation) {
        View view = this.getView();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 400);

        TranslatorFragment fragment = TranslatorFragment.newInstance(translation);

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
}
