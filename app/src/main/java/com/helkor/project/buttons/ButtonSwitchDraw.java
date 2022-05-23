package com.helkor.project.buttons;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.helkor.project.MainActivity;
import com.helkor.project.R;
import com.helkor.project.graphics.Bar;

public class ButtonSwitchDraw {
    private short button_variant;
    private ImageButton button_switch_draw;
    MainActivity activity;
    public ButtonSwitchDraw(MainActivity activity, int button_switch_draw_id){
        button_variant = 0;
        button_switch_draw = activity.findViewById(button_switch_draw_id);
        button_switch_draw.setVisibility(View.INVISIBLE);
        this.activity = activity;
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
                activity.shortSwitchButtonTriggered();
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
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.float_switch_button);
        button_switch_draw.startAnimation(animation);
        button_switch_draw.setVisibility(View.VISIBLE);
    }
    public void hide(){
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.float_switch_button_back);
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
