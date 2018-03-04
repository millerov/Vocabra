package com.example.alexmelnikov.vocabra.api;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.model.api.LanguageDetectionResult;
import com.example.alexmelnikov.vocabra.model.api.TranslationDirs;
import com.example.alexmelnikov.vocabra.model.api.TranslationResult;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorPresenter;
import com.example.alexmelnikov.vocabra.utils.Constants;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AlexMelnikov on 28.02.18.
 */

public class ApiHelper {
    private ApiService mService;

    private LanguagesRepository mLangRep;

    private Translation nextTranslation;

    public ApiHelper(){
        mLangRep = new LanguagesRepository();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.API_BASE_URL)
                .build();
        mService = retrofit.create(ApiService.class);
    }


    //Перенести setText в presenter!
    public void translateAsync(String text, String lang, TranslatorPresenter mTranslatorPresenter) throws IOException {


        mService.translate(Constants.API_KEY, text, lang).enqueue(new Callback<TranslationResult>() {

            @Override
            public void onResponse(Call<TranslationResult> call, Response<TranslationResult> response) {
                Log.d("API", response.toString());
                if (response.body() != null){
                    Log.d("MyTag", TextUtils.unescape(response.body().getText().toString()));
                    nextTranslation = new Translation(0, lang, text, TextUtils.unescape(response.body().getText().toString()), false);
                    mTranslatorPresenter.translationResultPassed(nextTranslation);
                }
            }

            @Override
            public void onFailure(Call<TranslationResult> call, Throwable t) {
               Log.d("MyTag","Error");
               Log.d("API", t.toString());
               mTranslatorPresenter.translationResultError();
            }
        });
    }


    public void getLanguagesAndSaveToDB(){

        mService.getLangs(Constants.API_KEY, "ru").enqueue(new Callback<TranslationDirs>() {

            @Override
            public void onResponse(Call<TranslationDirs> call, Response<TranslationDirs> response) {
                Log.d("API", response.toString());
                if (response.body() != null){

                    ArrayList<Language> transformed = new ArrayList<>();

                    LinkedHashMap<String, String> data = response.body().getLangs();

                    if (data != null) {
                        for (String key : data.keySet()) {
                            String value = data.get(key);
                            transformed.add(new Language(key, value));
                        }
                    }


                    if (!transformed.isEmpty() && mLangRep.getLanguagesFromDB().size() == 0) {
                        Log.d("MyTag", "Updating database");
                        mLangRep.insertLanguagesToDB(transformed);
                    }


                 }
            }

            @Override
            public void onFailure(Call<TranslationDirs> call, Throwable t) {
                Log.d("MyTag","Error");
                Log.d("API", t.toString());
            }
        });

    }





}
