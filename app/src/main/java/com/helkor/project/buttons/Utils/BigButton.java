package com.helkor.project.buttons.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.VibrationEffect;
import android.view.View;
import android.view.animation.AnimationUtils;

//Big button is holdable (with vibration effect) and clickable, can be showed and hided with animation.

public class BigButton extends LittleButton {

    private Listener listener;

    public interface Listener{
        void onHoldBigButton();
        void onClickBigButton();
    }

    protected BigButton(int button_view_id,Activity activity, Object implementation_context){
        super(button_view_id,activity);
        trySetListener(implementation_context);
        setOnClickListener(button_view);
    }
    protected BigButton(int button_view_id,Activity activity, Object implementation_context, int button_show_id) {
        this(button_view_id,activity,implementation_context);
        button_show_animation = AnimationUtils.loadAnimation(activity,button_show_id);
    }
    protected BigButton(int button_view_id,Activity activity, Object implementation_context, int button_show_id, int button_hide_id) {
        this(button_view_id,activity,implementation_context,button_show_id);
        button_hide_animation = AnimationUtils.loadAnimation(activity,button_hide_id);
    }
    @Override
    protected void trySetListener(Object implementation_context){
        try {
            listener = (Listener) implementation_context;
        } catch (ClassCastException e){
            throw new ClassCastException(implementation_context.toString()
                    + " must implement Listener");
        }
    }
    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void setOnClickListener(View view){

        button_view.setOnClickListener(v -> {
            listener.onClickBigButton();
        });

        button_view.setOnLongClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            listener.onHoldBigButton();
            return true;
        });
    }
}
