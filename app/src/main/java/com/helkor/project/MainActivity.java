package com.helkor.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.helkor.project.buttons.ButtonStart;
import com.helkor.project.buttons.MapSensor;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.ui_view.ViewProvider;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public LineDrawer lineDrawer;
    public final String TAG = MainActivity.class.getSimpleName();
    public LocationUpdater locationUpdater;
    private LocationManager locationManager;
    static public MapView mapview;
    public TextView test_text;
    RelativeLayout relative;

    private MapObjectCollection mapObjects;
    private Handler animationHandler;

    public final int COMFORTABLE_ZOOM_LEVEL = 16;

    private InputListener inputListener = new InputListener() {
        private Point point;

        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            this.point = point;
            lineDrawer.addPoint(point);
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String MAPKIT_KEY = "521a5766-2b6f-457f-9724-9676c9fc0244";
        MapKitFactory.setApiKey(MAPKIT_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        mapview = findViewById(R.id.map);
        test_text = findViewById(R.id.test_text);
        relative = findViewById(R.id.relative_layout_1);

        mapview.getMap().setScrollGesturesEnabled(false);
        mapview.getMap().setZoomGesturesEnabled(false);
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

        ButtonStart buttonStart = new ButtonStart(this,locationUpdater,R.id.button_start);
        mapview.getMap().addInputListener(inputListener);
        lineDrawer = new LineDrawer(mapview);

        mapObjects = mapview.getMap().getMapObjects().addCollection();
        MapSensor mapSensor = new MapSensor(this,relative,mapview,lineDrawer);
        animationHandler = new Handler();

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