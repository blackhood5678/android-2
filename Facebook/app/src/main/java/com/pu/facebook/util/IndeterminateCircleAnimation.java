package com.pu.facebook.util;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.pu.facebook.Circle;

public class IndeterminateCircleAnimation extends Animation {
    private Circle circle;
    private int angle;

    public IndeterminateCircleAnimation(Circle circle, int angle) {
        this.circle = circle;
        this.angle = angle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = (this.angle * interpolatedTime);

        circle.setAngle(angle);
        circle.requestLayout();
    }
}
