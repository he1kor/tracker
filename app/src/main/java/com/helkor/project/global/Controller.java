package com.helkor.project.global;

import android.app.Activity;

import com.helkor.project.R;
import com.helkor.project.activities.MainActivity;
import com.helkor.project.buttons.ButtonStart;
import com.helkor.project.buttons.ButtonSwitchDraw;
import com.helkor.project.draw.LineDrawer;
import com.helkor.project.draw.LocationSensor;
import com.helkor.project.draw.TouchSensor;
import com.helkor.project.graphics.Background;
import com.helkor.project.graphics.Bar;
import com.helkor.project.map.MapState;
import com.helkor.project.map.NavigatorState;
import com.yandex.mapkit.MapKit;


public class Controller {

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

    private ButtonStart button_start;
    private ButtonSwitchDraw button_switch_draw;


    private final float COMFORTABLE_ZOOM_LEVEL = 17.5f;

    public Controller(MainActivity activity,MapKit map_kit){
        this.main_activity = activity;
        this.map_kit = map_kit;
        map_state = new MapState(this,map_kit);
    }

    public void setup(){
        line_drawer = new LineDrawer(this,map_state);
        location_sensor = new LocationSensor(this,map_state,COMFORTABLE_ZOOM_LEVEL,line_drawer,map_kit);
        navigator_state = new NavigatorState(this,map_state);
        map_sensor = new TouchSensor(this,R.id.relative_layout_1,map_state,line_drawer);
    }

    public void initButtons (){
        map_state.show();
        Background.vanishing(main_activity);
        Bar.setActivity(main_activity);
        Bar.animateColor(main_activity.getColor(R.color.black),main_activity.getColor(R.color.light_red),2000);

        button_start = new ButtonStart(this,R.id.button_start);
        button_switch_draw = new ButtonSwitchDraw(this,R.id.button_switch_draw);

        button_start.show();
        setCommonMode();
        location_sensor.moveCamera(location_sensor.getMyLocation(), COMFORTABLE_ZOOM_LEVEL+1,3);
    }

    public void updatePathValue(double path){
        button_start.updateView(path);
    }
    public void updatePathValue(double path,double travelled_path){
        button_start.updateView(path,travelled_path);
    }


    public void holdMainButtonTriggered(){
        switch (button_start.getButtonVariant()) {
            case DRAW:
                button_switch_draw.hide();
                button_start.setButtonVariant(ButtonStart.Variant.MAIN);
                break;

            case MAIN:
                button_start.setButtonVariant(ButtonStart.Variant.DRAW);
                break;

            case WALK:
            case PAUSE:
            case FINISH:
                button_start.setButtonVariant(ButtonStart.Variant.MAIN);
                break;
        }
        holdMainButtonCheckout();
    }
    public void holdMainButtonCheckout(){
        Bar.stop();
        switch (button_start.getButtonVariant()) {
            case DRAW:
                setDrawMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL,1);
                break;

            case MAIN:
                setCommonMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                break;
        }
    }


    public void shortMainButtonTriggered(){
        Bar.stop();
        switch (button_start.getButtonVariant()) {
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
                button_start.setButtonVariant(ButtonStart.Variant.WALK);
                break;

            case WALK:
                setPausedMode();
                button_start.setButtonVariant(ButtonStart.Variant.PAUSE);
                break;

            case PAUSE:
                setWalkingMode();
                button_start.setButtonVariant(ButtonStart.Variant.WALK);
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
        map_sensor.hide();
        location_sensor.setDrawable(false);
        location_sensor.setWalkable(false);
        line_drawer.resetTravelledPath();

    }
    private void setDrawMode(){
        button_switch_draw.show();
        location_sensor.setDrawable(true);
        shortSwitchButtonCheckout();

    }
    private void setWalkingMode() {
        location_sensor.setWalkable(true);
    }
    private void setPausedMode() {
        location_sensor.setWalkable(false);
    }
    public void setFinishedMode(){
        location_sensor.setWalkable(false);
        button_start.setButtonVariant(ButtonStart.Variant.FINISH);
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
