package com.helkor.project.graphics;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.helkor.project.R;

public class Background {
    private static ValueAnimator value_animator;
    private static boolean loaded = false;
    public static void vanishing(Activity activity){
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.background_vanishing);
        activity.findViewById(R.id.background).startAnimation(animation);
    }
    public static void start(){
        value_animator.start();
    }
    public static void loadAnimationLoadingText(Activity activity){
        TextView text_view = activity.findViewById(R.id.loading);
        value_animator = ValueAnimator.ofFloat(0f, 1.0f);
        value_animator.setDuration(700);
        value_animator.setRepeatMode(ValueAnimator.REVERSE);
        value_animator.setRepeatCount(1);
        value_animator.addUpdateListener(valueAnimator -> {
            text_view.setAlpha((float) valueAnimator.getAnimatedValue());
            if (loaded){
                valueAnimator.cancel();
                loadAnimationVanishing(text_view, text_view.getAlpha());
            }
        });
        value_animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (!loaded) start();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        start();
    }
    public static void loadAnimationVanishing(TextView text_view,float alpha){
        System.out.println("IT'S COOOOMINGG!!!!!");
        value_animator = ValueAnimator.ofFloat(alpha, 0.0f);
        value_animator.setDuration((long) (700 * alpha));
        value_animator.setRepeatMode(ValueAnimator.REVERSE);
        value_animator.addUpdateListener(valueAnimator -> {
            text_view.setAlpha((float) valueAnimator.getAnimatedValue());
        });
        start();
    }
    public static void setLoaded(boolean loaded){
        Background.loaded = loaded;
    }
}
