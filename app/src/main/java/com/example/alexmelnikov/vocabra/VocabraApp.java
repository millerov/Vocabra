package com.example.alexmelnikov.vocabra;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.alexmelnikov.vocabra.api.ApiHelper;
import com.example.alexmelnikov.vocabra.dagger.AppComponent;
import com.example.alexmelnikov.vocabra.dagger.DaggerAppComponent;
import com.example.alexmelnikov.vocabra.dagger.DaggerPresenterComponent;
import com.example.alexmelnikov.vocabra.dagger.PresenterComponent;
import com.orhanobut.hawk.Hawk;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by AlexMelnikov on 28.02.18.
 */

public class VocabraApp extends Application {
    private static ApiHelper apiHelper;
    private static PresenterComponent presenterComponent;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        apiHelper = new ApiHelper();
        apiHelper.getLanguagesAndSaveToDB();

        presenterComponent = DaggerPresenterComponent.builder().build();
        appComponent = DaggerAppComponent.builder().build();

        Hawk.init(this).build();
        Realm.init(this);

        //REALM MIGRATION
        /* RealmConfiguration config2 = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config2);*/
    }


    public static PresenterComponent getPresenterComponent() {
        return presenterComponent;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static ApiHelper getApiHelper() {
        return apiHelper;
    }

}
