package com.example.alexmelnikov.vocabra.dagger;

import com.example.alexmelnikov.vocabra.dagger.module.RepositoryModule;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserPresenter;
import com.example.alexmelnikov.vocabra.ui.deck_add.DeckAddPresenter;
import com.example.alexmelnikov.vocabra.ui.translation.TranslationPresenter;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

@Singleton
@Component(
        modules = {
                RepositoryModule.class
        }
)
public interface PresenterComponent {

    void inject(TranslatorPresenter presenter);

    void inject(TranslationPresenter presenter);

    void inject(CardBrowserPresenter presenter);

    void inject(DeckAddPresenter presenter);
}
