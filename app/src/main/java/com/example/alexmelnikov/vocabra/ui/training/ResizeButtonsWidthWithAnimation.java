package com.example.alexmelnikov.vocabra.ui.training;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by AlexMelnikov on 04.04.18.
 */

public class ResizeButtonsWidthWithAnimation extends Animation {

    final int targetWidth;
    View view;
    View view2;
    int startWidth;

    public ResizeButtonsWidthWithAnimation(View view, View view2, int targetWidth, int startWidth) {
        this.view = view;
        this.view2 = view2;
        this.targetWidth = targetWidth;
        this.startWidth = startWidth;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = (int) (startWidth +(targetWidth - startWidth) * interpolatedTime);
        view.getLayoutParams().width = newWidth;
        view.requestLayout();
        view2.getLayoutParams().width = newWidth;
        view2.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}