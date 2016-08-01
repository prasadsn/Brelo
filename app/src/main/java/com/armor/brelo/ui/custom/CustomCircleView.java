package com.armor.brelo.ui.custom;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by prsn0001 on 8/1/2016.
 */
public class CustomCircleView extends View {

    private ObjectAnimator radiusAnimator;
    private float radius;
    private Paint circlePaint;
    private int color;

    public CustomCircleView(Context context) {
        super(context);
        circlePaint = new Paint();
    }

    public CustomCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint = new Paint();
    }

    public CustomCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circlePaint = new Paint();
    }

    public void setRadius(float value){
        this.radius = value;
        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
    }


    public void startAnimation(int animationDuration) {

        if (radiusAnimator == null || !radiusAnimator.isRunning()) {

            // Define what value the radius is supposed to have at specific time values
            Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
            Keyframe kf2 = Keyframe.ofFloat(0.5f, 180f);
            Keyframe kf1 = Keyframe.ofFloat(1f, 360f);

            // If you pass in the radius, it will be calling setRadius method, so make sure you have it!!!!!
            PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("radius", kf0, kf1, kf2);
            radiusAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvhRotation);
            radiusAnimator.setInterpolator(new LinearInterpolator());
            radiusAnimator.setDuration(animationDuration);
            radiusAnimator.start();
        }
        else {
            Log.d("Circle", "I am already running!");
        }
    }

    public void stopAnimation() {
        if (radiusAnimator != null) {
            radiusAnimator.cancel();
            radiusAnimator = null;
        }
    }

    public boolean getAnimationRunning() {
        return radiusAnimator != null && radiusAnimator.isRunning();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        circlePaint.setColor(color);
        canvas.drawCircle(360, 360, radius, circlePaint);
    }
}