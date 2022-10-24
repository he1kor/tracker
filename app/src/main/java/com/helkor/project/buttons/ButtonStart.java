package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Spanned;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.helkor.project.R;
import com.helkor.project.global.Controller;
import com.helkor.project.graphics.Bar;
import com.helkor.project.graphics.ColorVariable;

public class ButtonStart implements ColorVariable {
    private final Controller controller;

    private final Activity activity;
    private final Button button_start;
    private Variant variant;

    public ButtonStart(Controller controller, int button_start_id) {
        this.controller = controller;
        activity = controller.getMainActivity();

        variant = Variant.MAIN;
        button_start = activity.findViewById(button_start_id);
        button_start.setVisibility(View.INVISIBLE);

        Listener();
    }

    @SuppressLint("ClickableViewAccessibility")
    void Listener(){
        button_start.setOnClickListener(v -> {
            System.out.println("clicked");
            controller.shortMainButtonTriggered();
        });

        button_start.setOnLongClickListener(v -> {
            System.out.println("long clicked");
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            controller.holdMainButtonTriggered();
            return true;
        });
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void updateView(){
        switch (variant) {
            case MAIN:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_1));
                button_start.setTextSize(38);
                button_start.setText("Start");
                Bar.setColor(R.color.light_red);
                break;
            case DRAW:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_2));
                button_start.setTextSize(38);
                button_start.setText("Clear");
                Bar.setColor(R.color.lilac);
                break;
            case WALK:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_4));
                button_start.setTextSize(37);
                button_start.setText("Pause");
                Bar.setColor(R.color.green);
                break;
            case PAUSE:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_3));
                button_start.setTextSize(28);
                button_start.setText("Resume");
                Bar.setColor(R.color.yellow);
                break;
            case FINISH:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_4));
                button_start.setTextSize(34);
                button_start.setText("Finish!");
                Bar.setColor(R.color.green);
                break;
        }
    }
    public void show(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.button_float);
        button_start.setVisibility(View.VISIBLE);
        button_start.startAnimation(animation);
    }
    @Override
    public void setVariant(Variant variant) {
        this.variant = variant;
        updateView();
    }

    @Override
    public Variant getVariant() {
        return variant;
    }
}
