package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by AlexMelnikov on 22.03.18.
 */

public class CardsLinearLayoutManager extends LinearLayoutManager {

    public CardsLinearLayoutManager(Context context) {
        super(context);
    }

    public CardsLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CardsLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
