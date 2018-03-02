package com.example.alexmelnikov.vocabra.dagger.module;

import com.example.alexmelnikov.vocabra.data.LanguagesRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by AlexMelnikov on 01.03.18.
 */

@Module
public class ApiHelperModule {

    @Provides
    LanguagesRepository provideLangRep() {
        return new LanguagesRepository();
    }
}
