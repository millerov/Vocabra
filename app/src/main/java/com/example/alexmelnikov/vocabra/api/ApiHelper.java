package com.example.alexmelnikov.vocabra.api;

import android.util.Log;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.model.api.LanguageDetectionResult;
import com.example.alexmelnikov.vocabra.model.api.TranslationResult;
import com.example.alexmelnikov.vocabra.utils.Constants;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;

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


    public ApiHelper(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.API_BASE_URL)
                .build();
        mService = retrofit.create(ApiService.class);
    }

    public void translateAsync(String text, String lang, final TextView shown) throws IOException {


        mService.translate(Constants.API_KEY, text, lang).enqueue(new Callback<TranslationResult>() {

            @Override
            public void onResponse(Call<TranslationResult> call, Response<TranslationResult> response) {
                Log.d("API", response.toString());
                if (response.body() != null){
                    Log.d("MyTag", TextUtils.unescape(response.body().getText().toString()));
                    shown.setText(TextUtils.unescape(response.body().getText().toString()));
                }
            }

            @Override
            public void onFailure(Call<TranslationResult> call, Throwable t) {
               Log.d("MyTag","Error");
               shown.setText("Error");
            }
        });

    }
}
