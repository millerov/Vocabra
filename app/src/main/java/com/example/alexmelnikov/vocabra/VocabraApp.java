package com.example.alexmelnikov.vocabra;

import android.app.Application;
import android.content.res.Configuration;

import com.example.alexmelnikov.vocabra.dagger.DaggerAppComponent;
import com.example.alexmelnikov.vocabra.dagger.AppComponent;

/**
 * Created by AlexMelnikov on 28.02.18.
 */

public class VocabraApp extends Application {
    public static VocabraApp INSTANCE;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder().build();

        }
        return appComponent;
    }
}
