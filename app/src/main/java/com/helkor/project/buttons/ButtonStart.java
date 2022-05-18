package com.helkor.project.buttons;

import static android.provider.Settings.Global.getString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.helkor.project.MainActivity;
import com.helkor.project.R;

public class ButtonStart {
    private boolean isOnHold = false;
    private short button_variant;
    private long time_after_hold;
    private Button button_start;

    public ButtonStart(MainActivity activity, int button_start_id) {
        button_variant = -1;
        time_after_hold = -100;
        button_start = activity.findViewById(button_start_id);
        Listener(activity,button_start);
    }

    @SuppressLint("ClickableViewAccessibility")
    void Listener(MainActivity activity, Button button_start){

        button_start.setOnClickListener(v -> {
            if (!isOnHold && System.currentTimeMillis() - time_after_hold > 15 ) {
                activity.shortButtonTrigger();
            }
        });

        button_start.setOnLongClickListener(v -> {
            isOnHold = true;
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            activity.holdButtonTrigger();
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
    private void updateView(MainActivity activity){
        switch (button_variant) {
            case (0):
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_1));
                setText(activity.getResources().getString(R.string.button_variant_1));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = activity.getWindow();
                    window.setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), R.color.light_red, null));
                }
                break;
            case (1):
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_2));
                setText(activity.getResources().getString(R.string.button_variant_2));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = activity.getWindow();
                    window.setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), R.color.lilac, null));
                }
                break;
        }
    }
    public void setButtonVariant(int button_variant,MainActivity activity){
        this.button_variant = (short)button_variant;
        updateView(activity);
    }
    private void setText(String text){
        button_start.setText(text);
    }

    public short getButtonVariant() {
        return button_variant;
    }
}
