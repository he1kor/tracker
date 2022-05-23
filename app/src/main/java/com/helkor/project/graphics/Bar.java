package com.helkor.project.graphics;

import android.animation.ValueAnimator;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.helkor.project.MainActivity;
import com.helkor.project.R;

public class Bar {
    private static MainActivity activity;
    private static ValueAnimator anim;
    static ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

    public static void setColor(int color){
        activity.getWindow().setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), color, null));
    }

    public static void animateColor(int from_color, int to_color, int time) {
        final int status_bar_color = from_color;
        final int status_bar_to_color = to_color;

        anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorUpdateListener = this;
                float position = animation.getAnimatedFraction();

                int blended = Colors.blend(status_bar_color, status_bar_to_color, position);
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
