package com.example.alexmelnikov.vocabra.dagger;

import com.example.alexmelnikov.vocabra.dagger.module.TranslatorModule;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by AlexMelnikov on 28.02.18.
 */

@Singleton
@Component(modules = {
        TranslatorModule.class
})
public interface AppComponent {

    void inject (TranslatorFragment fragment);

}
