package com.example.alexmelnikov.vocabra.dagger;

import com.example.alexmelnikov.vocabra.api.ApiHelper;
import com.example.alexmelnikov.vocabra.dagger.module.ApiHelperModule;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by AlexMelnikov on 10.03.18.
 */


@Singleton
@Component(modules = {
})
public interface AppComponent {

    void inject(TranslatorFragment fragment);

    void inject(ApiHelper apiHelper);

}