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
import com.helkor.project.R;
import com.helkor.project.buttons.Utils.ButtonVariant;
import com.helkor.project.global.Controller;

public class ButtonSwitchDraw {

    private final Controller controller;
    Activity activity;

    ButtonVariant<Variant> button_variant;
    private ImageButton button_switch_draw;

    public enum Variant{
        DRAW,
        GPS
    }
    public ButtonSwitchDraw(Controller controller, int button_switch_draw_id){

        this.controller = controller;
        activity = controller.getMainActivity();
        button_switch_draw = activity.findViewById(button_switch_draw_id);
        button_switch_draw.setVisibility(View.INVISIBLE);
        button_variant = new ButtonVariant<>(Variant.class);
        listener();
        updateView();
        System.out.println(button_variant.toString());
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
    private void updateView(){
        switch (getButtonVariant()) {
            case DRAW:
                button_switch_draw.setImageResource(R.drawable.icon_draw_mode);
                break;
            case GPS:
                button_switch_draw.setImageResource(R.drawable.icon_gps_mode);
                break;
        }
    }
    public void show(){
        button_switch_draw.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.lilac)));
        button_switch_draw.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.float_switch_button_show);
        button_switch_draw.startAnimation(animation);
    }
    public void hide(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.float_switch_button_hide);
        button_switch_draw.startAnimation(animation);

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
        button_switch_draw.setVisibility(View.GONE);
    }
    public Variant nextVariant(){
        Variant variant = button_variant.next();
        updateView();
        return variant;
    }
    public Variant getButtonVariant() {
        return button_variant.face();
    }
}
