package com.example.alexmelnikov.vocabra.utils;

import android.util.Log;

import java.util.Map;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

public class MapHelper {
    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            Log.d("MyTag", "getKeyFromValue: " + o);
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }
}
