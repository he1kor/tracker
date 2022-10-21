package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.helkor.project.R;
import com.helkor.project.global.Controller;
import com.helkor.project.graphics.Bar;

public class ButtonStart {
    private final Controller controller;

    private final Activity activity;
    private final Button button_start;
    private Variant button_variant;
    private boolean isOnHold = false;
    private long time_after_hold;
    private int int_path;
    private int int_traveled_path;

    public enum Variant{
        MAIN,
        DRAW,
        WALK,
        PAUSE,
        FINISH
    }

    public ButtonStart(Controller controller, int button_start_id) {
        this.controller = controller;
        activity = controller.getMainActivity();

        int_path = 0;
        int_traveled_path = 0;
        button_variant = Variant.MAIN;
        time_after_hold = -100;
        button_start = activity.findViewById(button_start_id);
        button_start.setVisibility(View.INVISIBLE);

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
    private void updateView(){
        switch (button_variant) {
            case MAIN:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_1));
                setText(HtmlCompat.fromHtml("<b><big>" + "Start" + "</big></b>" +  "<br />" +
                        "<small>" + int_path + " m" + "</small>",HtmlCompat.FROM_HTML_MODE_COMPACT));
                Bar.setColor(R.color.light_red);
                break;
            case DRAW:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_2));
                setText(HtmlCompat.fromHtml("<b><big>" + "Clear" + "</big></b>" +  "<br />" +
                        "<small>" + int_path + "m" + "</small>",HtmlCompat.FROM_HTML_MODE_COMPACT));
                Bar.setColor(R.color.lilac);
                break;
            case WALK:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_4));
                setText(HtmlCompat.fromHtml("<b><big>" + "Pause" + "</big></b>" +  "<br />" +
                        "<small>" + int_traveled_path + " m /" + "<br />" + int_path + " m" + "</small>",HtmlCompat.FROM_HTML_MODE_COMPACT));
                Bar.setColor(R.color.green);
                break;
            case PAUSE:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_3));
                setText(HtmlCompat.fromHtml("<b><big>" + "Resume" + "</big></b>" +  "<br />" +
                        "<small>" + int_traveled_path + " m /" + "<br />" + int_path + " m" + "</small>",HtmlCompat.FROM_HTML_MODE_COMPACT));
                Bar.setColor(R.color.yellow);
                break;
            case FINISH:
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_4));
                setText(HtmlCompat.fromHtml("<b><medium>" + "Finished!" + "</medium></b>" +  "<br />" +
                        "<small>" + int_path + " m" + "</small>",HtmlCompat.FROM_HTML_MODE_COMPACT));
                Bar.setColor(R.color.green);
                break;
        }
    }
    public void updateView(double path){
        int_path = (int) path;
        updateView();
    }
    public void updateView(double path,double travelled_path){
        int_path = (int) path;
        int_traveled_path = (int) travelled_path;
        updateView();
    }
    public void show(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.button_float);
        button_start.setVisibility(View.VISIBLE);
        button_start.startAnimation(animation);
    }
    public void setButtonVariant(Variant button_variant){
        this.button_variant = button_variant;
        updateView();
    }
    private void setText(Spanned text){
        button_start.setText(text);
    }

    public Variant getButtonVariant() {
        return button_variant;
    }
}
