package com.helkor.project.graphics;

import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

public class Bar {
    private static Activity activity;
    private static ValueAnimator anim;
    static ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

    public static void setColorID(int colorID){
        activity.getWindow().setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), colorID, null));
        activity.getWindow().setNavigationBarColor(ResourcesCompat.getColor(activity.getResources(), colorID, null));
    }
    public static void setColor(int color){
        activity.getWindow().setStatusBarColor(color);
        activity.getWindow().setNavigationBarColor(color);
    }

    public static void animateColor(int to_color, int time) {

        anim = ValueAnimator.ofObject(new ArgbEvaluator(), activity.getWindow().getStatusBarColor(), to_color);
        anim.setInterpolator(new AccelerateInterpolator(2f));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorUpdateListener = this;
                setColor((int)animation.getAnimatedValue());
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
