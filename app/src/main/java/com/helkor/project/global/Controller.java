package com.helkor.project.global;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.helkor.project.R;
import com.helkor.project.activities.MainActivity;
import com.helkor.project.buttons.ClearButton;
import com.helkor.project.buttons.SwitchInputButton;
import com.helkor.project.buttons.Utils.LittleButton;
import com.helkor.project.buttons.MainButton;
import com.helkor.project.buttons.Utils.HideToColor;
import com.helkor.project.dialogs.ClearConfirmDialogFragment;
import com.helkor.project.dialogs.LeaveWalkingConfirmDialogFragment;
import com.helkor.project.draw.LineDrawer;
import com.helkor.project.draw.LocationSensor;
import com.helkor.project.draw.TouchSensor;
import com.helkor.project.graphics.Background;
import com.helkor.project.graphics.Bar;
import com.helkor.project.graphics.ColorVariable;
import com.helkor.project.map.MapState;
import com.helkor.project.map.NavigatorState;
import com.helkor.project.monitors.CounterMonitor;
import com.helkor.project.monitors.util.PathString;
import com.helkor.project.monitors.util.Time;
import com.helkor.project.monitors.util.Timer;
import com.yandex.mapkit.MapKit;

public class Controller implements LocationSensor.CameraInitListener,LineDrawer.FinishListener,Timer.Listener, MainButton.Listener,LittleButton.Listener, ClearConfirmDialogFragment.Listener, LeaveWalkingConfirmDialogFragment.Listener {


    public Activity getMainActivity() {
        return main_activity;
    }

    private final MainActivity main_activity;
    private final MapKit map_kit;

    private final MapState map_state;
    private NavigatorState navigator_state;

    private LineDrawer line_drawer;

    private LocationSensor location_sensor;
    private TouchSensor map_sensor;

    private CounterMonitor counter_monitor;
    private Timer timer;
    private PathString path_string;

    private MainButton main_button;
    private SwitchInputButton button_switch_input;
    private ClearButton button_clear;



    private enum DrawMode{
        FINGER,
        LOCATION;
    };
    private DrawMode draw_mode;
    private final float COMFORTABLE_ZOOM_LEVEL = 17.5f;

    public Controller(MainActivity activity,MapKit map_kit){
        this.main_activity = activity;
        this.map_kit = map_kit;
        map_state = new MapState(this,map_kit);
    }

    public void setup(){
        counter_monitor = new CounterMonitor(main_activity,R.id.pedometer_layout_outside,R.id.pedometer_layout,R.id.pedometer_text1,R.id.pedometer_text2);
        timer = new Timer(this);
        path_string = new PathString(counter_monitor, PathString.BOTTOM);
        line_drawer = new LineDrawer(map_state);

        navigator_state = new NavigatorState(getMainActivity(),map_state);
        location_sensor = new LocationSensor(line_drawer,this,map_state.getMapView(),map_kit,COMFORTABLE_ZOOM_LEVEL);
        map_sensor = new TouchSensor(line_drawer,getMainActivity().findViewById(R.id.relative_layout_1),map_state.getMapView());

        line_drawer.addPathListener(path_string);
        line_drawer.addFinishListener(this);
        Background.loadAnimationLoadingText(main_activity);
    }

    @Override
    public void onCameraInitialized() {
        initButtons();
    }
    public void initButtons (){
        Background.setLoaded(true);
        map_state.show();
        Background.vanishing(main_activity);
        Bar.setActivity(main_activity);
        Bar.animateColor(main_activity.getColor(R.color.black),main_activity.getColor(R.color.light_red),2000);

        counter_monitor.animateAppearing();

        main_button = new MainButton(main_activity,this,R.id.button_start, R.anim.button_float);
        main_button.setVariant(MainButton.Variant.MAIN);
        main_button.show();

        button_switch_input = new SwitchInputButton(main_activity,this,R.id.button_switch_input,R.anim.switch_input_button_float_show,R.anim.switch_input_button_float_hide);
        button_clear = new ClearButton(main_activity,this,R.id.button_clear,R.anim.clear_button_float_show,R.anim.clear_button_float_hide);

        setCommonMode();
        location_sensor.moveCamera(location_sensor.getMyLocation(), COMFORTABLE_ZOOM_LEVEL+1,3);
    }

    @Override
    public void onHoldBigButton() {
        holdMainButtonGetOutOfState();
        holdMainButtonCheckout();
        Bar.stop();
    }

    @Override
    public void onClickBigButton() {
        clickMainButtonGetOutOfState();
        clickMainButtonCheckout();
        Bar.stop();
    }

    private void holdMainButtonGetOutOfState(){
        switch (main_button.getVariant()) {
            case DRAW:
                button_switch_input.hideWithColor(HideToColor.MAIN);
                button_clear.hideWithColor(HideToColor.MAIN);
                break;

            case MAIN:
            case WALK:
            case PAUSE:
                break;
            case FINISH:
                counter_monitor.switchModes();
                timer.stop();
                break;
        }
    }
    public void holdMainButtonCheckout(){
        Bar.stop();
        switch (main_button.getVariant()) {
            case MAIN:

                setDrawMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL,1);
                break;

            case PAUSE:
            case WALK:
                LeaveWalkingConfirmDialogFragment leave_walking_confirm_dialog_fragment = new LeaveWalkingConfirmDialogFragment(this);
                leave_walking_confirm_dialog_fragment.show(main_activity.getSupportFragmentManager(),"LeaveWalkingConfirmDialogFragment");
                break;
            case DRAW:
            case VIEW:
            case FINISH:
                setCommonMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                break;
        }
    }
    private void clickMainButtonGetOutOfState(){
        switch (main_button.getVariant()) {
            case DRAW:
            case VIEW:
            case WALK:
                break;

            case MAIN:
                if (location_sensor.getMyLocation() == null) {
                    location_sensor.setExpectingLocation(true);
                } else {
                    location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                }
                counter_monitor.switchModes();
                timer.start();
                break;

            case PAUSE:
                timer.resume();
                break;
        }
    }
    private void clickMainButtonCheckout(){
        switch (main_button.getVariant()) {
            case DRAW:
                setViewDrawMode();
                break;
            case VIEW:
                setDrawMode();
                break;
            case MAIN:
            case PAUSE:
                setWalkingMode();
                break;
            case WALK:
                setPausedMode();
                break;
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClickLittleButton(View view) {
        switch (view.getId()){
            case R.id.button_switch_input:
                button_switch_input.nextVariant();
                shortSwitchButtonCheckout();
                break;
            case R.id.button_clear:
                ClearConfirmDialogFragment clear_confirm_dialog_fragment = new ClearConfirmDialogFragment(this);
                clear_confirm_dialog_fragment.show(main_activity.getSupportFragmentManager(),"ClearConfirmDialogFragment");
                break;
        }

    }

    public void shortSwitchButtonCheckout(){
        switch (button_switch_input.getVariant()) {
            case GPS:
                setNavigatorDrawMode();
                break;
            case DRAW:
                setFingerDrawMode();
                break;
        }
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        switch (dialog.getTag()) {
            case "ClearConfirmDialogFragment":
                line_drawer.clear();
                break;
            case "LeaveWalkingConfirmDialogFragment":
                counter_monitor.switchModes();
                timer.stop();
                setCommonMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL + 1, 1);
                break;
            default:
                throw new RuntimeException("Unknown dialog");
        }
    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }


    @Override
    public void onTimeUpdated(Time time) {
        counter_monitor.setTopText(time.toString());
    }

    private void setCommonMode(){

        counter_monitor.setVariant(ColorVariable.Variant.MAIN);
        main_button.setVariant(MainButton.Variant.MAIN);

        path_string.setTravelledPathEnabled(false);

        map_sensor.turnOff();
        location_sensor.setDrawable(false);
        location_sensor.setWalkable(false);
        location_sensor.unSubscribeToLocationUpdate();

        line_drawer.resetTravelledPath();
    }

    private void setDrawMode(){

        counter_monitor.setVariant(ColorVariable.Variant.DRAW);
        main_button.setVariant(MainButton.Variant.DRAW);

        path_string.setTravelledPathEnabled(false);
        button_switch_input.show();
        button_clear.show();
        if (draw_mode == DrawMode.LOCATION){
            setNavigatorDrawMode();
        } else if (draw_mode == DrawMode.FINGER){
            setFingerDrawMode();
        }
        shortSwitchButtonCheckout();
    }

    private void setWalkingMode() {

        counter_monitor.setVariant(ColorVariable.Variant.WALK);
        main_button.setVariant(MainButton.Variant.WALK);

        path_string.setTravelledPathEnabled(true);
        location_sensor.setWalkable(true);
        location_sensor.subscribeToLocationUpdate();
    }

    private void setPausedMode() {

        counter_monitor.setVariant(ColorVariable.Variant.PAUSE);
        main_button.setVariant(MainButton.Variant.PAUSE);

        location_sensor.unSubscribeToLocationUpdate();
        location_sensor.setWalkable(false);
        timer.pause();
    }

    public void setFinishedMode(){

        Vibrator vibrator = (Vibrator) main_activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,200,100,200,100,50,100,50,100,300},-1));
        }

        counter_monitor.setVariant(ColorVariable.Variant.FINISH);
        main_button.setVariant(MainButton.Variant.FINISH);

        location_sensor.unSubscribeToLocationUpdate();
        location_sensor.setWalkable(false);
        timer.pause();
    }



    @Override
    public void onFinish() {
        setFinishedMode();
    }

    private void setFingerDrawMode(){
        draw_mode = DrawMode.FINGER;
        line_drawer.setDivisionStep(4);
        location_sensor.setDrawable(false);
        location_sensor.unSubscribeToLocationUpdate();
        map_sensor.turnOn();
    }

    private void setNavigatorDrawMode(){
        draw_mode = DrawMode.LOCATION;
        line_drawer.setDivisionStep(4);
        location_sensor.setDrawable(true);
        location_sensor.subscribeToLocationUpdate();
        map_sensor.turnOff();
    }
    private void setViewDrawMode() {
        main_button.setVariant(ColorVariable.Variant.VIEW);
        counter_monitor.setVariant(ColorVariable.Variant.VIEW);

        button_switch_input.hideWithColor(HideToColor.VIEW);
        button_clear.hideWithColor(HideToColor.VIEW);

        location_sensor.setDrawable(false);
        location_sensor.unSubscribeToLocationUpdate();
        map_sensor.turnOff();
    }




    public void onStop() {
        map_state.onStop();
    }

    public void onStart() {
        map_state.onStart(location_sensor);
    }

    public MapState getMapState(){
        return map_state;
    }

    public NavigatorState getNavigatorState(){
        return navigator_state;
    }
}
