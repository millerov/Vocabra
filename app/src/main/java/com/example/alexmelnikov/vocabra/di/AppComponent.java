package com.example.alexmelnikov.vocabra.di;

import com.example.alexmelnikov.vocabra.ui.main.MainActivity;

/**
 * Created by AlexMelnikov on 25.02.18.
 */


/*@Singleton
@Component(modules = {

})*/
public interface AppComponent {
    void inject (MainActivity mainActivity);
}
