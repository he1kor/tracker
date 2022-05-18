package com.helkor.project.buttons;

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

import com.helkor.project.LocationUpdater;
import com.helkor.project.MainActivity;
import com.helkor.project.R;

public class ButtonStart {
    private boolean isOnHold = false;
    private short button_variant;
    private long time_after_hold = -100;
    public ButtonStart(MainActivity activity, LocationUpdater locationUpdater, int button_start_id,short button_variant) {
        this.button_variant = button_variant;
        Button button_start = activity.findViewById(button_start_id);
        set_view(activity,button_start);
        Listener(activity,locationUpdater,button_start);
    }
        @SuppressLint("ClickableViewAccessibility")
        void Listener(MainActivity activity, LocationUpdater locationUpdater, Button button_start){

        button_start.setOnClickListener(v -> {
            if (!isOnHold && System.currentTimeMillis() - time_after_hold > 15 ) {
                if (locationUpdater.getMyLocation() == null) {
                    locationUpdater.setExpectingLocation(true);
                } else {
                    locationUpdater.moveCamera(activity, locationUpdater.getMyLocation(), activity.COMFORTABLE_ZOOM_LEVEL);
                }
            }
        });

        button_start.setOnLongClickListener(v -> {
            isOnHold = true;
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            switch (button_variant) {
                case (0):
                    button_variant = 1;
                    break;
                case (1):
                    button_variant = 0;
                    break;
            }
            activity.holdButtonTrigger();
            set_view(activity,button_start);
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
    private void set_view(MainActivity activity,Button button_start){
        switch (button_variant) {
            case (0):
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_1));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = activity.getWindow();
                    window.setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), R.color.light_red, null));
                }
                break;
            case (1):
                button_start.setBackground(ContextCompat.getDrawable(button_start.getContext(), R.drawable.circle_variant_2));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = activity.getWindow();
                    window.setStatusBarColor(ResourcesCompat.getColor(activity.getResources(), R.color.lilac, null));
                }
                break;
        }
    }

    public short GetButtonVariant() {
        return button_variant;
    }
}
