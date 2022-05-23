package com.helkor.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helkor.project.buttons.ButtonStart;
import com.helkor.project.draw.LineDrawer;
import com.helkor.project.draw.MapSensor;
import com.helkor.project.graphics.Bar;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();
    public LocationUpdater locationUpdater;
    private LocationManager locationManager;
    static public MapView map_view;
    static public TextView test_text;
    RelativeLayout drawable_relative;
    RelativeLayout background;
    ButtonStart button_start;
    LineDrawer line_drawer;
    MapSensor mapSensor;
    boolean is_clicked_once = false;

    public final int COMFORTABLE_ZOOM_LEVEL = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String MAPKIT_KEY = "521a5766-2b6f-457f-9724-9676c9fc0244";
        MapKitFactory.setApiKey(MAPKIT_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        map_view = findViewById(R.id.map);
        test_text = findViewById(R.id.test_text);
        drawable_relative = findViewById(R.id.relative_layout_1);
        background = findViewById(R.id.background);

        map_view.getMap().setScrollGesturesEnabled(true);
        map_view.getMap().setZoomGesturesEnabled(true);
        map_view.getMap().setTiltGesturesEnabled(true);
        map_view.getMap().setMapType(MapType.VECTOR_MAP);
        map_view.getMap().setModelsEnabled(true);
        map_view.getMap().set2DMode(false);

        locationManager = MapKitFactory.getInstance().createLocationManager();

        MapKit mapKit = MapKitFactory.getInstance();

        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(map_view.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);


        line_drawer = new LineDrawer(map_view,this);
        locationUpdater = new LocationUpdater(this,map_view,COMFORTABLE_ZOOM_LEVEL,line_drawer);
        mapSensor = new MapSensor(drawable_relative,map_view,line_drawer);


    }
    void initButton(){
        playBackgroundAnim(background);
        Bar.setActivity(this);
        Bar.animateColor(this.getColor(R.color.black),this.getColor(R.color.light_red),2000);
        map_view.setVisibility(View.VISIBLE);
        button_start = new ButtonStart(this,R.id.button_start);
        this.holdButtonTriggered();
    }
    void playBackgroundAnim(RelativeLayout background){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.background_vanishing);
        background.startAnimation(animation);
    }

    //TODO: decide the final interface for different modes (add another button?)
    public void holdButtonTriggered(){
        switch (button_start.getButtonVariant()) {
            case (-1):
                //From starting mode switch to common mode:
                button_start.playAnimation();
                button_start.setButtonVariant(0);
                setCommonMode();
                line_drawer.setMinRealStep(20);
                locationUpdater.setDrawable(true);
                locationUpdater.moveCamera(map_view,locationUpdater.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,3);
                break;
            case (1):
                //From draw mode switch to common mode:
                button_start.setButtonVariant(0);
                setCommonMode();
                line_drawer.setMinRealStep(20);
                locationUpdater.setDrawable(true);
                locationUpdater.moveCamera(map_view,locationUpdater.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                Bar.stop();
                break;
            case (0):
                //From common mode switch to draw mode:
                button_start.setButtonVariant(1);
                setDrawMode();
                line_drawer.setMinRealStep(5);
                locationUpdater.setDrawable(false);
                locationUpdater.moveCamera(map_view,locationUpdater.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL,1);
                Bar.stop();
                break;
        }
    }
    public void shortButtonTriggered(){
        switch (button_start.getButtonVariant()) {
            case (1):
                //In draw mode:
                line_drawer.clear(map_view);
                locationUpdater.moveCamera(map_view,locationUpdater.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL,1);
                break;
            case (0):
                //In common mode:
                if (locationUpdater.getMyLocation() == null) {
                    locationUpdater.setExpectingLocation(true);
                } else {
                    locationUpdater.moveCamera(map_view,locationUpdater.getMyLocation(), this.COMFORTABLE_ZOOM_LEVEL+1,1);
                }
                break;
        }
    }

    private void setCommonMode(){
        drawable_relative.setVisibility(View.INVISIBLE);
    }
    private void setDrawMode(){
        drawable_relative.setVisibility(View.VISIBLE);
    }


    private void subscribeToLocationUpdate() {
        if (locationManager != null && locationUpdater.getMyLocationListener() != null) {
            double DESIRED_ACCURACY = 0;
            long MINIMAL_TIME = 300;
            double MINIMAL_DISTANCE = 1;
            boolean USE_IN_BACKGROUND = true;
            locationManager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, locationUpdater.getMyLocationListener());
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        map_view.onStop();
        MapKitFactory.getInstance().onStop();

    }
    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        map_view.onStart();
        subscribeToLocationUpdate();
    }
}