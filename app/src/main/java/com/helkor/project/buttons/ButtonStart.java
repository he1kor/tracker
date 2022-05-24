package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.helkor.project.activities.MainActivity;
import com.helkor.project.R;
import com.helkor.project.global.Controller;
import com.helkor.project.global.YandexMapkit;
import com.helkor.project.graphics.Bar;

public class ButtonStart {
    private final Controller controller;

    private Activity activity;
    private boolean isOnHold = false;
    private short button_variant;
    private long time_after_hold;
    private Button button_start;

    public ButtonStart(Controller controller, int button_start_id) {
        this.controller = controller;
        activity = controller.getActivity();

        button_variant = -1;
        time_after_hold = -100;
        button_start = activity.findViewById(button_start_id);
        button_start.setVisibility(View.INVISIBLE);
        this.activity = activity;

        Listener();
    }

    @SuppressLint("ClickableViewAccessibility")
    void Listener(){
        button_start.setOnClickListener(v -> {
            if (!isOnHold && System.currentTimeMillis() - time_after_hold > 15 ) {
                controller.shortMainButtonTriggered();
            }
        });

        button_start.setOnLongClickListener(v -> {
            isOnHold = true;
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            controller.holdMainButtonTriggered();
            return false;
        });

        button_start.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isOnHold) {
                    time_after_hold = System.currentTimeMillis();
                    isOnHold = false;
                }
                return false;
            }
            return false;
        });
    }
    public void updateView(){
        switch (button_variant) {
            case (0):
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_1));
                setText(activity.getResources().getString(R.string.button_variant_1));
                Bar.setColor(R.color.light_red);
                break;
            case (1):
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_2));
                setText(activity.getResources().getString(R.string.button_variant_2));
                Bar.setColor(R.color.lilac);
                break;
        }
    }
    public void show(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.button_float);
        button_start.setVisibility(View.VISIBLE);
        button_start.startAnimation(animation);
    }
    public void setButtonVariant(int button_variant){
        this.button_variant = (short)button_variant;
        updateView();
    }
    private void setText(String text){
        button_start.setText(text);
    }

    public short getButtonVariant() {
        return button_variant;
    }
}
