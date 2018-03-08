package com.example.alexmelnikov.vocabra.ui;

import com.example.alexmelnikov.vocabra.model.Translation;

/**
 * For presenters (TranslatorPresenter and TranslationPresenter) for both to be able
 * to get translation from ApiHelper
 */

public interface Translating {
    void translationRequested(String data);
    void translationResultPassed(Translation translation);
    void translationResultError();
}
