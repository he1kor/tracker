package com.helkor.project.buttons;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.helkor.project.activities.MainActivity;
import com.helkor.project.R;
import com.helkor.project.global.Controller;
import com.helkor.project.global.YandexMapkit;

public class ButtonSwitchDraw {

    private final Controller controller;
    Activity activity;

    private short button_variant;
    private ImageButton button_switch_draw;
    public ButtonSwitchDraw(Controller controller, int button_switch_draw_id){

        this.controller = controller;
        activity = controller.getMainActivity();

        button_variant = 0;
        button_switch_draw = activity.findViewById(button_switch_draw_id);
        button_switch_draw.setVisibility(View.INVISIBLE);
        listener();
        updateView();
    }
    void listener(){
        button_switch_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                controller.shortSwitchButtonTriggered();
            }
        });
    }
    public void playAnimation(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.button_float);
        button_switch_draw.setVisibility(View.VISIBLE);
        button_switch_draw.startAnimation(animation);
    }
    private void updateView(){
        switch (button_variant) {
            case (0):
                button_switch_draw.setImageResource(R.drawable.icon_draw_mode);
                break;
            case (1):
                button_switch_draw.setImageResource(R.drawable.icon_gps_mode);
                break;
        }
    }
    public void show(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.float_switch_button_show);

        button_switch_draw.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.lilac)));

        button_switch_draw.startAnimation(animation);
        button_switch_draw.setVisibility(View.VISIBLE);
    }
    public void hide(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.float_switch_button_hide);

        int colorFrom = activity.getColor(R.color.lilac);
        int colorTo = activity.getColor(R.color.light_red);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                button_switch_draw.setBackgroundTintList(ColorStateList.valueOf((int)animator.getAnimatedValue()));
            }

        });
        colorAnimation.start();
        button_switch_draw.startAnimation(animation);
        button_switch_draw.setVisibility(View.GONE);
    }
    public void setButtonVariant(int button_variant){
        this.button_variant = (short)button_variant;
        updateView();
    }
    public short getButtonVariant() {
        return button_variant;
    }
}
