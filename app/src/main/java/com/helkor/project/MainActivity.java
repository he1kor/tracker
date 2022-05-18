package com.helkor.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helkor.project.buttons.ButtonStart;
import com.helkor.project.buttons.MapSensor;
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
    static public MapView mapview;
    static public TextView test_text;
    RelativeLayout drawable_relative;
    ButtonStart start_button;
    LineDrawer lineDrawer;
    MapSensor mapSensor;

    public final int COMFORTABLE_ZOOM_LEVEL = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String MAPKIT_KEY = "521a5766-2b6f-457f-9724-9676c9fc0244";
        MapKitFactory.setApiKey(MAPKIT_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        mapview = findViewById(R.id.map);
        test_text = findViewById(R.id.test_text);
        drawable_relative = findViewById(R.id.relative_layout_1);

        mapview.getMap().setScrollGesturesEnabled(true);
        mapview.getMap().setZoomGesturesEnabled(true);
        mapview.getMap().setTiltGesturesEnabled(false);
        mapview.getMap().setMapType(MapType.NONE);
        mapview.getMap().setModelsEnabled(false);
        mapview.getMap().set2DMode(true);

        locationManager = MapKitFactory.getInstance().createLocationManager();

        locationUpdater = new LocationUpdater(this,COMFORTABLE_ZOOM_LEVEL);

        MapKit mapKit = MapKitFactory.getInstance();

        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        start_button = new ButtonStart(this,locationUpdater,R.id.button_start, (short) 0);
        holdButtonTrigger();
        lineDrawer = new LineDrawer(mapview);
        mapSensor = new MapSensor(this,drawable_relative,mapview,lineDrawer);
    }
    public void holdButtonTrigger(){
        switch (start_button.GetButtonVariant()){
            case (0):
                setCommonMode();
                break;
            case (1):
                setDrawMode();
                break;
        }
    }
    public String boolToString (boolean b){
        if (b) return "True";
        else return "False";
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
        mapview.onStop();
        MapKitFactory.getInstance().onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();
        subscribeToLocationUpdate();
    }
}