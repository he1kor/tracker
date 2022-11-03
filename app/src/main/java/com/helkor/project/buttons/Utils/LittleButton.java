package com.helkor.project.buttons.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

//Little button is clickable, can be showed and hided with animation.

public abstract class LittleButton {

    private Listener listener;
    protected Button button_view;
    protected Animation button_show_animation;
    protected Animation button_hide_animation;
    protected final Vibrator vibrator;

    public interface Listener{
        void onClickLittleButton(View view);
    }
    //without button_view
    protected LittleButton(Activity activity,Object implementation_context){
        this.vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        trySetListener(implementation_context);
    }
    protected LittleButton(Activity activity,Object implementation_context, int button_show_id){
        this(activity,implementation_context);
        button_show_animation = AnimationUtils.loadAnimation(activity,button_show_id);
    }
    protected LittleButton(Activity activity,Object implementation_context, int button_show_id, int button_hide_id){
        this(activity,implementation_context,button_show_id);
        button_hide_animation = AnimationUtils.loadAnimation(activity,button_hide_id);
    }
    //without listener
    protected LittleButton(int button_view_id,Activity activity){
        this(activity);
        button_view = activity.findViewById(button_view_id);
        button_view.setVisibility(View.INVISIBLE);
    }
        protected LittleButton(int button_view_id,Activity activity,int button_show_id) {
            this(button_view_id,activity);
            button_show_animation = AnimationUtils.loadAnimation(activity,button_show_id);
        }
        protected LittleButton(int button_view_id,Activity activity, int button_show_id, int button_hide_id) {
            this(button_view_id,activity,button_show_id);
            button_hide_animation = AnimationUtils.loadAnimation(activity,button_hide_id);
        }
    //without listener and button_view

    protected LittleButton(Activity activity){
        this.vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }
        protected LittleButton(Activity activity, int button_show_id){
            this(activity);
            button_show_animation = AnimationUtils.loadAnimation(activity,button_show_id);
        }
        protected LittleButton(Activity activity, int button_show_id, int button_hide_id){
            this(activity,button_show_id);
            button_hide_animation = AnimationUtils.loadAnimation(activity,button_hide_id);
        }


    protected LittleButton(int button_view_id,Activity activity, Object implementation_context){
        this(button_view_id,activity);
        trySetListener(implementation_context);
    }
    protected LittleButton(int button_view_id,Activity activity, Object implementation_context, int button_show_id) {
        this(button_view_id,activity,implementation_context);
        button_show_animation = AnimationUtils.loadAnimation(activity,button_show_id);
    }
    protected LittleButton(int button_view_id,Activity activity, Object implementation_context, int button_show_id, int button_hide_id) {
        this(button_view_id,activity,implementation_context,button_show_id);
        button_hide_animation = AnimationUtils.loadAnimation(activity,button_hide_id);
    }


    protected void trySetListener(Object implementation_context){
        try {
            listener = (Listener) implementation_context;
        } catch (ClassCastException e){
            throw new ClassCastException(implementation_context.toString()
                    + " must implement Listener");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setOnClickListener(View view){
        view.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            listener.onClickLittleButton(view);
        });
    }

    public void show(){
        button_view.setVisibility(View.VISIBLE);
        if (button_show_animation != null){
            button_view.startAnimation(button_show_animation);
        }
    }
    public void hide(){
        button_view.setVisibility(View.INVISIBLE);
        if (button_hide_animation != null){
            button_view.startAnimation(button_hide_animation);
        }
    }
}
