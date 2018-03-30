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

    public static String getFirstLanguageIndexFromDir(String langs) {
        return langs.substring(0, langs.indexOf("-"));
    }

    public static String getSecondLanguageIndexFromDir(String langs) {
        return langs.substring(langs.indexOf("-")+1, langs.length());
    }

    public static String getRightWordEnding(int number, String[] words) {
        int i = number % 100;
        if (i > 19)
            i %= 10;
        switch (i) {
            case 1: {
                return words[0];
            }
            case 2:case 3:case 4: {
                return words[1];
            }
            default: {
                return words[2];
            }
        }
    }
}
