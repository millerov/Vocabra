package com.example.alexmelnikov.vocabra;

import android.app.Application;
import android.content.res.Configuration;

import com.example.alexmelnikov.vocabra.api.ApiHelper;
import com.example.alexmelnikov.vocabra.dagger.DaggerAppComponent;
import com.example.alexmelnikov.vocabra.dagger.AppComponent;

/**
 * Created by AlexMelnikov on 28.02.18.
 */

public class VocabraApp extends Application {
    private AppComponent appComponent;
    private static ApiHelper apiHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        apiHelper = new ApiHelper();
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder().build();

        }
        return appComponent;
    }

    public static ApiHelper getApiHelper() {
        return apiHelper;
    }
}
