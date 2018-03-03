package com.example.alexmelnikov.vocabra.utils;

/**
 * Created by golde on 11.04.2017.
 */

public class TextUtils {
    public static String unescape(String data){
        return data.substring(1, data.length() - 1);
    }

    public static String getTranslationDir(String langFrom, String langTo) {
        String from = LanguageUtils.findKeyByName(langFrom);
        String to = LanguageUtils.findKeyByName(langTo);
        return from + "-" + to;
    }
}
