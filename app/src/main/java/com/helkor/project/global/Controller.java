package com.helkor.project.global;

import android.app.Activity;
import android.widget.TextView;


import com.helkor.project.R;
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

    public Activity getActivity() {
        return activity;
    }

    private Activity activity;
    private MapKit map_kit;

    private MapState map_state;
    private NavigatorState navigator_state;

    private LineDrawer line_drawer;

    private LocationSensor location_sensor;
    private TouchSensor map_sensor;

    private ButtonStart button_start;
    private ButtonSwitchDraw button_switch_draw;


    private final float COMFORTABLE_ZOOM_LEVEL = 18.5f;

    public Controller(Activity activity,MapKit map_kit){
        this.activity = activity;
        this.map_kit = map_kit;
        setup();
    }

    public void setup(){
        map_state = new MapState(this,map_kit,R.id.map);

        line_drawer = new LineDrawer(this,map_state);
        location_sensor = new LocationSensor(this,map_state,COMFORTABLE_ZOOM_LEVEL,line_drawer,map_kit);
        navigator_state = new NavigatorState(this,map_state);
        map_sensor = new TouchSensor(this,R.id.relative_layout_1,map_state,line_drawer);
        System.out.println("setuped");
    }

    public void test(String text){
        TextView test = activity.findViewById(R.id.test_text);
        test.setText(text);
    }

    public void initButtons (){
        System.out.println("showing");
        map_state.show();
        Background.vanishing(activity);
        Bar.setActivity(activity);
        Bar.animateColor(activity.getColor(R.color.black),activity.getColor(R.color.light_red),2000);

        button_start = new ButtonStart(this,R.id.button_start);
        button_switch_draw = new ButtonSwitchDraw(this,R.id.button_switch_draw);

        button_start.show();
        button_start.setButtonVariant(0);
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
            case (1):
                //From draw mode switch to main mode:
                button_switch_draw.hide();
                button_start.setButtonVariant(0);
                break;
            case (0):
                //From main mode switch to draw mode:
                button_start.setButtonVariant(1);
                break;
            case (2):
                //From walking mode switch to main mode:
                button_start.setButtonVariant(0);
                break;
            case (3):
                //From paused mode switch to main mode:
                button_start.setButtonVariant(0);
                break;
            case (4):
                //From paused mode switch to main mode:
                button_start.setButtonVariant(0);
                break;
        }
        holdMainButtonCheckout();
    }
    public void holdMainButtonCheckout(){
        Bar.stop();
        switch (button_start.getButtonVariant()) {
            case (1):
                setDrawMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL,1);
                break;
            case (0):
                setCommonMode();
                location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                break;
        }
    }


    public void shortMainButtonTriggered(){
        Bar.stop();
        switch (button_start.getButtonVariant()) {
            case (1):
                //In draw mode:
                line_drawer.clear();
                break;
            case (0):
                //In common mode:
                if (location_sensor.getMyLocation() == null) {
                    location_sensor.setExpectingLocation(true);
                } else {
                    location_sensor.moveCamera(location_sensor.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                }
                setWalkingMode();
                button_start.setButtonVariant(2);
                break;
            case (2):
                //In walking mode:
                setPausedMode();
                button_start.setButtonVariant(3);
                break;
            case (3):
                //In paused mode:
                setWalkingMode();
                button_start.setButtonVariant(2);
                break;
        }
    }
    public void shortSwitchButtonTriggered(){
        switch (button_switch_draw.getButtonVariant()) {
            case (1):
                //From starting mode switch to navigator mode:
                button_switch_draw.setButtonVariant(0);
                break;
            case (0):
                //From navigator mode switch to draw mode:
                button_switch_draw.setButtonVariant(1);
                break;

        }
        shortSwitchButtonCheckout();
    }
    public void shortSwitchButtonCheckout(){
        switch (button_switch_draw.getButtonVariant()) {
            case (1):
                setNavigatorDrawMode();
                break;
            case (0):
                setFingerDrawMode();
                break;
        }
    }






    private void setCommonMode(){
        map_sensor.hide();
        location_sensor.setDrawable(false);
        location_sensor.setWalkable(false);
        line_drawer.resetWalkedPath();

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
        button_start.setButtonVariant(4);
    }




    private void setFingerDrawMode(){
        map_sensor.show();
        line_drawer.setMinRealStep(5);
        line_drawer.setDivisionStep(10);
        location_sensor.setDrawable(false);
    }
    private void setNavigatorDrawMode(){
        line_drawer.setMinRealStep(4);
        line_drawer.setDivisionStep(8);
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
