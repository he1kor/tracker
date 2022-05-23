package com.helkor.project.graphics;

import android.animation.ValueAnimator;

import com.helkor.project.MainActivity;

public class Bar {
    private static MainActivity activity;
    private static ValueAnimator anim;
    static ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    public static void animateColor(int from_color, int to_color, int time) {
        final int statusBarColor = from_color;
        final int statusBarToColor = to_color;

        anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorUpdateListener = this;
                float position = animation.getAnimatedFraction();

                int blended = Colors.blend(statusBarColor, statusBarToColor, position);
                activity.getWindow().setStatusBarColor(blended);
            }
        });

        anim.setDuration(time).start();
    }
    public static void stop(){
        anim.removeUpdateListener(animatorUpdateListener);
        anim.cancel();
    }
    public static void setActivity(MainActivity activity){
        Bar.activity = activity;
    }
}
