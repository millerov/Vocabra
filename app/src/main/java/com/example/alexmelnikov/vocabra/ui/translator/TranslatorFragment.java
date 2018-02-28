package com.example.alexmelnikov.vocabra.ui.translator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.api.ApiHelper;
import com.example.alexmelnikov.vocabra.api.ApiService;
import com.example.alexmelnikov.vocabra.model.api.TranslationResult;
import com.example.alexmelnikov.vocabra.utils.Constants;
import com.example.alexmelnikov.vocabra.utils.TextUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends MvpFragment {

    @BindView(R.id.ed_translate)
    EditText etTranslate;

    @BindView(R.id.tv_translated)
    TextView tvTranslated;


    @BindView(R.id.btn_clear)
    ImageButton btnClear;

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
                 .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                 .map(charSequence -> charSequence.toString())
                 .filter(text -> !text.isEmpty())
                 .subscribe(text -> {
                     Log.d("MyTag", "Text:" + text);
                     translate(text);
                     Log.d("MyTag", "Text went to translate");
                 });
    }


    /*
     Hardcoded language
     */


    public void translate(String data) {
        try {
            VocabraApp.getApiHelper().translateAsync(data, "ru-en", tvTranslated);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

