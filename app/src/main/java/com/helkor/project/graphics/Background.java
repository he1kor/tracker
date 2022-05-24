package com.helkor.project.graphics;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.helkor.project.R;

public class Background {
    public static void vanishing(Activity activity){
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.background_vanishing);
        activity.findViewById(R.id.background).startAnimation(animation);
    }
}
