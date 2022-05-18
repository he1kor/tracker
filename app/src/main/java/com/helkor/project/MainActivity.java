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
        createMapObjects();


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
    static public void test(){
        Toast toast = Toast.makeText(MainActivity.mapview.getContext(),
                "test", Toast.LENGTH_SHORT);
        toast.show();
    }

    private MapObjectTapListener circleMapObjectTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            if (mapObject instanceof CircleMapObject) {
                CircleMapObject circle = (CircleMapObject)mapObject;

                float randomRadius = 100.0f + 50.0f * new Random().nextFloat();

                Circle curGeometry = circle.getGeometry();
                Circle newGeometry = new Circle(curGeometry.getCenter(), randomRadius);
                circle.setGeometry(newGeometry);

                Object userData = circle.getUserData();
                if (userData instanceof CircleMapObjectUserData) {
                    CircleMapObjectUserData circleUserData = (CircleMapObjectUserData)userData;

                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "Circle with id " + circleUserData.id + " and description '"
                                    + circleUserData.description + "' tapped",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            return true;
        }
    };

    private class CircleMapObjectUserData {
        final int id;
        final String description;

        CircleMapObjectUserData(int id, String description) {
            this.id = id;
            this.description = description;
        }
    }
    private void createMapObjects() {
        final Point CAMERA_TARGET = new Point(59.952, 30.318);
        final Point ANIMATED_RECTANGLE_CENTER = new Point(59.956, 30.313);
        final Point TRIANGLE_CENTER = new Point(59.948, 30.313);
        final Point POLYLINE_CENTER = CAMERA_TARGET;
        final Point CIRCLE_CENTER = new Point(59.956, 30.323);
        final Point DRAGGABLE_PLACEMARK_CENTER = new Point(59.948, 30.323);
        final Point ANIMATED_PLACEMARK_CENTER = new Point(59.948, 30.318);
        final double OBJECT_SIZE = 0.0015;
        ArrayList<Point> polylinePoints = new ArrayList<>();
        polylinePoints.add(new Point(
                POLYLINE_CENTER.getLatitude() + OBJECT_SIZE,
                POLYLINE_CENTER.getLongitude()- OBJECT_SIZE));
        polylinePoints.add(new Point(
                POLYLINE_CENTER.getLatitude() - OBJECT_SIZE,
                POLYLINE_CENTER.getLongitude()- OBJECT_SIZE));
        polylinePoints.add(new Point(
                POLYLINE_CENTER.getLatitude(),
                POLYLINE_CENTER.getLongitude() + OBJECT_SIZE));

        PolylineMapObject polyline = mapObjects.addPolyline(new Polyline(polylinePoints));
        polyline.setStrokeColor(Color.BLACK);
        polyline.setZIndex(100.0f);

        PlacemarkMapObject mark = mapObjects.addPlacemark(DRAGGABLE_PLACEMARK_CENTER);
        mark.setOpacity(0.5f);
        mark.setIcon(ImageProvider.fromResource(this, R.drawable.mark));
        mark.setDraggable(true);

        createPlacemarkMapObjectWithViewProvider();
    }
    private void createTappableCircle() {
        final Point CIRCLE_CENTER = new Point(59.956, 30.323);
        CircleMapObject circle = mapObjects.addCircle(
                new Circle(CIRCLE_CENTER, 100), Color.GREEN, 2, Color.RED);
        circle.setZIndex(100.0f);
        circle.setUserData(new CircleMapObjectUserData(42, "Tappable circle"));

        // Client code must retain strong reference to the listener.
        circle.addTapListener(circleMapObjectTapListener);
    }
    private void createPlacemarkMapObjectWithViewProvider() {
        final TextView textView = new TextView(this);
        final int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLACK };
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        textView.setTextColor(Color.RED);
        textView.setText("Hello, World!");

        final ViewProvider viewProvider = new ViewProvider(textView);
        final PlacemarkMapObject viewPlacemark =
                mapObjects.addPlacemark(new Point(59.946263, 30.315181), viewProvider);

        final Random random = new Random();
        final int delayToShowInitialText = 5000;  // milliseconds
        final int delayToShowRandomText = 500; // milliseconds;

        // Show initial text `delayToShowInitialText` milliseconds and then
        // randomly change text in textView every `delayToShowRandomText` milliseconds
        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int randomInt = random.nextInt(1000);
                textView.setText("Some text version " + randomInt);
                textView.setTextColor(colors[randomInt % colors.length]);
                viewProvider.snapshot();
                viewPlacemark.setView(viewProvider);
                animationHandler.postDelayed(this, delayToShowRandomText);
            }
        }, delayToShowInitialText);
    }
}