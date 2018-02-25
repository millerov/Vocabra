package com.example.alexmelnikov.vocabra;

import android.app.Application;
import android.util.Log;

import com.example.alexmelnikov.vocabra.di.AppComponent;
import com.example.alexmelnikov.vocabra.di.DaggerAppComponent;


/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class VocabraApp extends Application {
    public static VocabraApp INSTANCE;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        appComponent = buildComponent();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder().build();
    }
}
