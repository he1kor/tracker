package com.helkor.project.buttons;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageButton;

import com.helkor.project.R;
import com.helkor.project.buttons.Utils.ButtonVariant;

import java.util.Objects;

public class ClearButton extends LittleButton {

    private final ImageButton button_view;

    private final int COLOR_MAIN;
    private final int COLOR_DRAW;
    private final long ANIMATION_DURATION = 500;

    public ClearButton(Activity activity, Object implementation_context, int button_view_id, int button_show_id, int button_hide_id){
        super(activity,implementation_context,button_show_id,button_hide_id);
        button_view = activity.findViewById(button_view_id);
        setOnClickListener(button_view);
        COLOR_MAIN = activity.getColor(R.color.light_red);
        COLOR_DRAW = activity.getColor(R.color.lilac);
    }
    @Override
    public void show(){
        button_view.setVisibility(View.VISIBLE);
        button_view.setBackgroundTintList(ColorStateList.valueOf(COLOR_DRAW));
        button_view.startAnimation(button_show_animation);
    }
    @Override
    public void hide(){
        button_view.startAnimation(button_hide_animation);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), COLOR_DRAW, COLOR_MAIN);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                button_view.setBackgroundTintList(ColorStateList.valueOf((int)animator.getAnimatedValue()));
            }

        });
        colorAnimation.start();
        button_view.setVisibility(View.GONE);
    }
}
