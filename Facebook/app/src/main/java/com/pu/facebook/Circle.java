package com.pu.facebook;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.pu.facebook.util.IndeterminateCircleAnimation;

public class Circle extends View {
    private static int START_ANGLE_POINT = 90;
    private final Paint paint;
    private RectF rect;

    private int width, height, strokeWidth;
    private int tint;
    private float angle;
    private boolean indeterminateOnly;
    private IndeterminateProgressManager indeterminateProgressManager;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.Circle, 0, 0);
        strokeWidth = typedArray.getInt(R.styleable.Circle_stroke_width, 5);
        indeterminateOnly = typedArray.getBoolean(R.styleable.Circle_indeterminateOnly, true);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
        tint = typedArray.getColor(R.styleable.Circle_tint, ContextCompat.getColor(context, R.color.colorAccent));
        paint.setColor(tint);

        //Initial Angle (optional, it can be zero)
        angle = getAngle(typedArray.getInteger(R.styleable.Circle_progress, 100));
        if (indeterminateOnly) {
            indeterminateProgressManager = new IndeterminateProgressManager(this);
            indeterminateProgressManager.runProgress();
        } else {
            indeterminateProgressManager = null;
        }
    }

    public void setIndeterminateOnly(boolean indeterminateOnly) {
        this.indeterminateOnly = indeterminateOnly;
        invalidate();
    }

    public void setProgress(int per) {
        if (!indeterminateOnly) {
            CircleAngleAnimation animation = new CircleAngleAnimation(this, getAngle(per));
            animation.setDuration(1000);
            this.startAnimation(animation);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        rect = new RectF(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect != null) {
            canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    private int getAngle(int progress) {
        if (progress > 100)
            progress = 100;
        if (progress < 0)
            progress = 0;
        return (360 * progress) / 100;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE && indeterminateOnly) {
            indeterminateProgressManager.runProgress();
        }
    }

    private class IndeterminateProgressManager {
        private Circle circle;
        private int oldStartAngle = -1;
        private int startAngle = 90;


        public IndeterminateProgressManager(Circle circle) {
            this.circle = circle;
        }

        void runProgress() {
            Log.i(this.getClass().getName(), "Running Indeterminate progress..");
            IndeterminateCircleAnimation animation = new IndeterminateCircleAnimation(circle, 360);
            if (startAngle <= 360)
                Circle.START_ANGLE_POINT = startAngle;
            else
                Circle.START_ANGLE_POINT = 360;
            oldStartAngle = startAngle;
            if (oldStartAngle >= 360) {
                startAngle = 40;
            } else
                startAngle = oldStartAngle + 40;
            circle.startAnimation(animation);
            animation.setDuration(1000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (circle.getVisibility() == View.VISIBLE)
                        runProgress();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
