package com.helkor.project.graphics;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.helkor.project.activities.MainActivity;

public class Bar {
    private static Activity activity;
    private static ValueAnimator anim;
    static ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

    public static void setColor(int color){
        activity.getWindow().setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), color, null));
    }

    public static void animateColor(int from_color, int to_color, int time) {

        anim = ValueAnimator.ofObject(new ArgbEvaluator(), from_color, to_color);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorUpdateListener = this;
                activity.getWindow().setStatusBarColor((int)animation.getAnimatedValue());
            }
        });

        anim.setDuration(time).start();
    }
    public static void stop(){
        anim.removeUpdateListener(animatorUpdateListener);
        anim.cancel();
    }
    public static void setActivity(Activity activity){
        Bar.activity = activity;
    }
}
