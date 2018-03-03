package com.example.alexmelnikov.vocabra.data;

import android.support.annotation.NonNull;

import com.orhanobut.hawk.Hawk;

/**
 * Created by AlexMelnikov on 03.03.18.
 */

public class UserDataRepository {

    public void putValue(String key, @NonNull Object value) {
        Hawk.put(key, value);
    }

    public Object getValue(String key, @NonNull Object defaultValue) {
        if (!Hawk.contains(key))
            putValue(key, defaultValue);
        return Hawk.get(key);
    }

}
