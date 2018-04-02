package com.example.alexmelnikov.vocabra.utils;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by AlexMelnikov on 02.04.18.
 */

public class CardUtils {

    public static HashMap<String, Integer> getOptionsIncrementsToDateByLevel(int level) {
        HashMap<String, Integer> options = new HashMap<String, Integer>();
        int good;

        switch (level) {
            case 1:
                options.put("forgot", 0);
                options.put("good", 0);
                options.put("easy", 3);
                options.put("hard", 0);
                break;
            case 2:
                options.put("forgot", 0);
                options.put("good", 1);
                options.put("easy", 3);
                options.put("hard", 0);
                break;
            default:
                good = 3;
                while (level > 3) {
                    level--;
                    good *= 2;
                }
                options.put("forgot", 0);
                options.put("good", good);
                options.put("easy", good * 2);
                options.put("hard", good / 2);
                break;
        }

        return options;
    }
}
