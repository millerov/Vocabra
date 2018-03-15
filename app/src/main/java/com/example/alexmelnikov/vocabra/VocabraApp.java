package com.example.alexmelnikov.vocabra;

import android.app.Application;
import android.util.Log;

import com.example.alexmelnikov.vocabra.api.ApiHelper;
import com.example.alexmelnikov.vocabra.dagger.DaggerPresenterComponent;
import com.example.alexmelnikov.vocabra.dagger.PresenterComponent;
import com.orhanobut.hawk.Hawk;

import io.realm.Realm;

/**
 * Created by AlexMelnikov on 28.02.18.
 */

public class VocabraApp extends Application {
    private static ApiHelper apiHelper;
    private static PresenterComponent presenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        apiHelper = new ApiHelper();
        apiHelper.getLanguagesAndSaveToDB();

        presenterComponent = DaggerPresenterComponent.builder().build();

        Hawk.init(this).build();
        Realm.init(this);
    }


    public static PresenterComponent getPresenterComponent() {
        return presenterComponent;
    }

    public static ApiHelper getApiHelper() {
        return apiHelper;
    }
}
