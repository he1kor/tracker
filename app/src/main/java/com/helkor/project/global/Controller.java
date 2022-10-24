package com.helkor.project.global;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.helkor.project.R;
import com.helkor.project.activities.MainActivity;
import com.helkor.project.buttons.ButtonSwitchDraw;
import com.helkor.project.buttons.LittleButton;
import com.helkor.project.buttons.MainButton;
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


public class Controller implements Timer.Listener, MainButton.Listener, LittleButton.Listener{


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
    private ButtonSwitchDraw button_switch_draw;


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
        line_drawer = new LineDrawer(this,map_state);
        navigator_state = new NavigatorState(this,map_state);

        location_sensor = new LocationSensor(this,map_state,COMFORTABLE_ZOOM_LEVEL,line_drawer,map_kit);
        map_sensor = new TouchSensor(this,R.id.relative_layout_1,map_state,line_drawer);

        line_drawer.addListener(path_string);
    }

    public void initButtons (){
        map_state.show();
        Background.vanishing(main_activity);
        Bar.setActivity(main_activity);
        Bar.animateColor(main_activity.getColor(R.color.black),main_activity.getColor(R.color.light_red),2000);

        counter_monitor.animateAppearing();

        main_button = new MainButton(main_activity,this,R.id.button_start, R.anim.button_float);
        main_button.setVariant(MainButton.Variant.MAIN);
        main_button.show();

        button_switch_draw = new ButtonSwitchDraw(main_activity,this,R.id.button_switch_draw,R.anim.float_switch_button_show,R.anim.float_switch_button_hide);

        setCommonMode();
        location_sensor.moveCamera(location_sensor.getMyLocation(), COMFORTABLE_ZOOM_LEVEL+1,3);
    }

    @Override
    public void onHoldBigButton() {
        switch (main_button.getVariant()) {
            case DRAW:
                button_switch_draw.hide();
                break;

            case MAIN:
            case WALK:
                break;
            case FINISH:
            case PAUSE:
                counter_monitor.switchModes();
                timer.stop();
                break;
        }
        holdMainButtonCheckout();
    }

    @Override
    public void onClickBigButton() {
        Bar.stop();
        switch (main_button.getVariant()) {
            case DRAW:
                line_drawer.clear();
                break;

            case MAIN:
                if (location_sensor.getMyLocation() == null) {
                    location_sensor.setExpectingLocation(true);
                } else {
                    location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                }
                setWalkingMode();
                counter_monitor.switchModes();
                timer.start();
                break;

            case WALK:
                setPausedMode();
                break;

            case PAUSE:
                timer.resume();
                setWalkingMode();
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

            case DRAW:
            case FINISH:
            case PAUSE:
                setCommonMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                break;
        }
    }
    @Override
    public void onClickLittleButton() {
        button_switch_draw.nextVariant();
        shortSwitchButtonCheckout();
    }

    public void shortSwitchButtonCheckout(){
        switch (button_switch_draw.getVariant()) {
            case GPS:
                setNavigatorDrawMode();
                break;
            case DRAW:
                setFingerDrawMode();
                break;
        }
    }


    @Override
    public void onTimeUpdated(Time time) {
        counter_monitor.setTopText(time.toString());
    }


    private void setCommonMode(){

        counter_monitor.setVariant(ColorVariable.Variant.MAIN);
        main_button.setVariant(MainButton.Variant.MAIN);

        path_string.setTravelledPathEnabled(false);
        map_sensor.hide();
        location_sensor.setDrawable(false);
        location_sensor.setWalkable(false);
        line_drawer.resetTravelledPath();
    }
    private void setDrawMode(){

        counter_monitor.setVariant(ColorVariable.Variant.DRAW);
        main_button.setVariant(MainButton.Variant.DRAW);

        path_string.setTravelledPathEnabled(false);
        button_switch_draw.show();
        location_sensor.setDrawable(true);
        shortSwitchButtonCheckout();

    }

    private void setWalkingMode() {

        counter_monitor.setVariant(ColorVariable.Variant.WALK);
        main_button.setVariant(MainButton.Variant.WALK);

        path_string.setTravelledPathEnabled(true);
        location_sensor.setWalkable(true);
    }

    private void setPausedMode() {

        counter_monitor.setVariant(ColorVariable.Variant.PAUSE);
        main_button.setVariant(MainButton.Variant.PAUSE);

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

        location_sensor.setWalkable(false);
        timer.pause();
    }



    private void setFingerDrawMode(){
        map_sensor.show();
        line_drawer.setDivisionStep(4);
        location_sensor.setDrawable(false);
    }

    private void setNavigatorDrawMode(){
        line_drawer.setDivisionStep(4);
        location_sensor.setDrawable(true);
        map_sensor.hide();
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
