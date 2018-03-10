package com.example.alexmelnikov.vocabra.dagger.module;

import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

@Module
public class RepositoryModule {

    @Provides
    LanguagesRepository provideLanguagesRepository() {
        return new LanguagesRepository();
    }

    @Provides
    TranslationsRepository provideTranslationsRepository() {
        return new TranslationsRepository();
    }

    @Provides
    UserDataRepository provideUserDataRepository() {
        return new UserDataRepository();
    }
}
