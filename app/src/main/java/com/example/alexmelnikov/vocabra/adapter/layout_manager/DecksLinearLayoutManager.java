package com.example.alexmelnikov.vocabra.adapter.layout_manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

public class DecksLinearLayoutManager extends LinearLayoutManager {

    public DecksLinearLayoutManager(Context context) {
        super(context);
    }

    public DecksLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DecksLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
