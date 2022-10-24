package com.helkor.project.global;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.helkor.project.R;
import com.helkor.project.activities.MainActivity;
import com.helkor.project.buttons.ButtonStart;
import com.helkor.project.buttons.ButtonSwitchDraw;
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


public class Controller implements Timer.Listener{


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

    private ButtonStart button_start;
    private ButtonSwitchDraw button_switch_draw;


    private final float COMFORTABLE_ZOOM_LEVEL = 17.5f;

    public Controller(MainActivity activity,MapKit map_kit){
        this.main_activity = activity;
        this.map_kit = map_kit;
        map_state = new MapState(this,map_kit);
    }

    public void setup(){
        System.out.println("test");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counter_monitor = new CounterMonitor(main_activity,R.id.pedometer_layout_outside,R.id.pedometer_layout,R.id.pedometer_text1,R.id.pedometer_text2);
        timer = new Timer(this);
        System.out.println("test");
        path_string = new PathString(counter_monitor, PathString.BOTTOM);
        System.out.println("tes2");
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

        button_start = new ButtonStart(this,R.id.button_start);
        button_start.setVariant(ButtonStart.Variant.MAIN);
        button_switch_draw = new ButtonSwitchDraw(this,R.id.button_switch_draw);

        button_start.show();
        setCommonMode();
        location_sensor.moveCamera(location_sensor.getMyLocation(), COMFORTABLE_ZOOM_LEVEL+1,3);
    }


    public void holdMainButtonTriggered(){

        switch (button_start.getVariant()) {
            case DRAW:
                button_switch_draw.hide();
                break;

            case MAIN:
                break;

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
    public void holdMainButtonCheckout(){
        Bar.stop();
        switch (button_start.getVariant()) {
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


    public void shortMainButtonTriggered(){
        Bar.stop();
        switch (button_start.getVariant()) {
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
    public void shortSwitchButtonTriggered(){
        button_switch_draw.nextVariant();
        shortSwitchButtonCheckout();
    }
    public void shortSwitchButtonCheckout(){
        switch (button_switch_draw.getButtonVariant()) {
            case GPS:
                setNavigatorDrawMode();
                break;
            case DRAW:
                setFingerDrawMode();
                break;
        }
    }






    private void setCommonMode(){

        counter_monitor.setVariant(ColorVariable.Variant.MAIN);
        button_start.setVariant(ButtonStart.Variant.MAIN);

        path_string.setTravelledPathEnabled(false);
        map_sensor.hide();
        location_sensor.setDrawable(false);
        location_sensor.setWalkable(false);
        line_drawer.resetTravelledPath();
    }
    private void setDrawMode(){

        counter_monitor.setVariant(ColorVariable.Variant.DRAW);
        button_start.setVariant(ButtonStart.Variant.DRAW);

        path_string.setTravelledPathEnabled(false);
        button_switch_draw.show();
        location_sensor.setDrawable(true);
        shortSwitchButtonCheckout();

    }
    private void setWalkingMode() {

        counter_monitor.setVariant(ColorVariable.Variant.WALK);
        button_start.setVariant(ButtonStart.Variant.WALK);

        path_string.setTravelledPathEnabled(true);
        location_sensor.setWalkable(true);
    }
    private void setPausedMode() {

        counter_monitor.setVariant(ColorVariable.Variant.PAUSE);
        button_start.setVariant(ButtonStart.Variant.PAUSE);

        location_sensor.setWalkable(false);
        timer.pause();
    }
    public void setFinishedMode(){

        Vibrator vibrator = (Vibrator) main_activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,200,100,200,100,50,100,50,100,300},-1));
        }

        counter_monitor.setVariant(ColorVariable.Variant.FINISH);
        button_start.setVariant(ButtonStart.Variant.FINISH);

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

    @Override
    public void onTimeUpdated(Time time) {
        counter_monitor.setTopText(time.toString());
    }
}
