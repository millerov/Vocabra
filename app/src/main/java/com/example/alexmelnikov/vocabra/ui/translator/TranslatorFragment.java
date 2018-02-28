package com.example.alexmelnikov.vocabra.ui.translator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends MvpFragment {

    @BindView(R.id.ed_translate)
    EditText etTranslate;

    @BindView(R.id.tv_translated)
    TextView tvTranslated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translator, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTranslated.setMovementMethod(new ScrollingMovementMethod());
        setupRxListener();
    }

    public void setupRxListener() {
         RxTextView.textChanges(etTranslate)
                 .debounce(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                 .subscribe(text -> {
                     //App.getApiHelper().detectAsync(initialText, mInputLang);
                 });
    }





}

