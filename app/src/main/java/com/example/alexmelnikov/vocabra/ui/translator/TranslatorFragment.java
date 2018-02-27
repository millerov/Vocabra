package com.example.alexmelnikov.vocabra.ui.translator;

import android.database.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends MvpFragment {

    private static final String API_KEY = "AIzaSyBqcLNMZWFXoOpTsphw5kXJ_mJ0rsffiR0";

    @BindView(R.id.ed_translate)
    EditText etTranslate;

    @BindView(R.id.tv_translated)
    TextView tvTranslated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translator, container, false);

    }

/*    @Override
    public void onStart() {
        super.onStart();

        TranslateOptions options = TranslateOptions.newBuilder()
                .setApiKey(API_KEY)
                .build();
        Translate translate = options.getService();

        io.reactivex.Observable<String> obs = RxTextView.textChanges(tvTranslated)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .filter(charSequence -> charSequence.length() > 3)
                .map(charSequence -> charSequence.toString())
                .subscribeOn(Schedulers.io());


        obs.subscribe(string -> {
            Translation translation =
                    translate.translate(etTranslate.getText().toString(),
                            Translate.TranslateOption.targetLanguage("ru"));
            tvTranslated.setText(translation.getTranslatedText());
        });
    }*/





}

