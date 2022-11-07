package com.helkor.project.input.buttons;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageButton;

import com.helkor.project.R;
import com.helkor.project.input.buttons.Utils.HideToColor;
import com.helkor.project.input.buttons.Utils.LittleButton;

public class ConnectionButton extends LittleButton {

    private final int COLOR_MAIN;
    private final int COLOR_DRAW;
    private final int COLOR_VIEW;
    private final long ANIMATION_DURATION = 500;
    ValueAnimator colorAnimation;
    private final ImageButton button_view;

    public ConnectionButton(Activity activity, Object implementation_context, int button_view_id, int button_show_id, int button_hide_id) {
        super(activity, implementation_context, button_show_id, button_hide_id);
        button_view = activity.findViewById(button_view_id);
        setOnClickListener(button_view);
        COLOR_MAIN = activity.getColor(R.color.light_red);
        COLOR_DRAW = activity.getColor(R.color.lilac);
        COLOR_VIEW = activity.getColor(R.color.yellow);
    }
    @Override
    public void show(){
        if (colorAnimation != null && colorAnimation.isRunning()) colorAnimation.cancel();
        button_view.setVisibility(View.VISIBLE);
        button_view.setBackgroundTintList(ColorStateList.valueOf(COLOR_DRAW));
        button_view.startAnimation(button_show_animation);
    }
    @Override
    public void hide(){
        button_view.startAnimation(button_hide_animation);
        button_view.setVisibility(View.GONE);
    }
    private int getColor(HideToColor hide_to_color){
        switch (hide_to_color){
            case MAIN:
                return COLOR_MAIN;
            case VIEW:
                return COLOR_VIEW;
        }
        throw new RuntimeException("Unknown color");
    }
    public void hideWithColor(HideToColor hide_to_color){
        int color_to = getColor(hide_to_color);
        hide();
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), COLOR_DRAW, color_to);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                button_view.setBackgroundTintList(ColorStateList.valueOf((int)animator.getAnimatedValue()));
            }

        });
        colorAnimation.start();
    }
}
