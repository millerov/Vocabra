package com.example.alexmelnikov.vocabra.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexmelnikov.vocabra.model.SelectedLanguages;
import com.orhanobut.hawk.Hawk;

/**
 * Created by AlexMelnikov on 03.03.18.
 */

public class UserDataRepository {

    public static final String SELECTED_LANGUAGES = "SELECTED_LANGUAGES";
    public static final String SELECTED_CARD_SORT_METHOD = "SELECTED_CARD_SORT_METHOD";
    public static final String FIRST_APP_LAUNCH_DATE = "FIRST_APP_LAUNCH_DATE";

    public void putValue(String key, @NonNull Object value) {
        Hawk.put(key, value);
    }

    public Object getValue(String key, @NonNull Object defaultValue) {
        if (!Hawk.contains(key))
            putValue(key, defaultValue);
        return Hawk.get(key);
    }

}
