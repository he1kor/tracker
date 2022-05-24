package com.helkor.project.draw;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.helkor.project.activities.MainActivity;
import com.helkor.project.draw.LineDrawer;
import com.helkor.project.global.Controller;
import com.helkor.project.global.YandexMapkit;
import com.helkor.project.map.MapState;
import com.helkor.project.tech.map.ShortLocationArray;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

public class LocationSensor{
    private final Controller controller;

    private CameraListener myCameraListener;
    private LocationListener myLocationListener;
    private Activity activity;
    private MapView map_view;
    private Map map;
    private LineDrawer line_drawer;
    private ShortLocationArray last_locations;
    private final int MAX_CHECK_LINE = 3;
    private boolean isMapLoaded;

    private boolean is_drawable = false;
    private boolean is_walkable = false;
    private final float COMFORTABLE_ZOOM_LEVEL;

    private boolean expecting_location;
    private float expecting_zoom;
    private LocationManager location_manager;

    private Point myLocation;
    public LocationSensor(Controller controller, MapState map_state, float COMFORTABLE_ZOOM_LEVEL, LineDrawer line_drawer){

        this.controller = controller;
        activity = controller.getActivity();

        this.map_view = map_state.getMapView();
        this.map = map_view.getMap();
        this.COMFORTABLE_ZOOM_LEVEL = COMFORTABLE_ZOOM_LEVEL;
        this.line_drawer = line_drawer;
        last_locations = new ShortLocationArray(MAX_CHECK_LINE);
        isMapLoaded = false;
        location_manager = map_state.getMapKit().createLocationManager();

        Listener();
    }
    private void updateLocation(Location location){
        last_locations.add(location);
    }

    private void Listener() {

        myLocationListener = new LocationListener() {

            @Override
            public void onLocationUpdated(@NonNull Location location) {
                last_locations.add(location);
                if (is_walkable) {
                    line_drawer.checkForTravelled(location.getPosition(),location.getAccuracy());
                }
                if (is_drawable && last_locations.size() == MAX_CHECK_LINE) addPoint();

                if (myLocation == null) {
                    firstMoveCamera(location.getPosition(), 16.25f);
                }
                if (expecting_location) {
                    moveCamera(location.getPosition(), expecting_zoom,1);
                    setExpectingLocation(false);
                }
                myLocation = location.getPosition();
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    System.out.println("Location Status is not aviable.");
                }
            }
        };
        myCameraListener = new CameraListener() {
            @Override
            public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {
                //moveCamera(activity, myLocation, COMFORTABLE_ZOOM_LEVEL);
            }
        };
    }
    private void addPoint(){

        Point lastDrewPoint = line_drawer.getLastPoint();
        if (lastDrewPoint != null) {
            if (Geo.distance(lastDrewPoint, last_locations.getLastPoint()) > last_locations.getLastAccuracy())
                line_drawer.addPoint(last_locations.getLastPoint());
            else if (Geo.distance(line_drawer.getLastPoint(),last_locations.getLastPoint()) > 5) line_drawer.addPhantomPoint(last_locations.getLastPoint());
        }
        else line_drawer.addPoint(last_locations.getMinimalAccuracyPoint().getPoint());
    }
    public void moveCamera(Point point, float zoom,float duration) {
        if (point != null) {
            map.move(
                    new CameraPosition(point, zoom, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, duration),
                    null
            );
        }
        else {
            expecting_zoom = zoom;
            expecting_location = true;
        }
    }
    private void firstMoveCamera(Point point, float zoom){
        map.move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.LINEAR, 0),
                new Map.CameraCallback() {
                    @Override
                    public void onMoveFinished(boolean b) {
                        controller.initButtons();
                    }
                }
        );
    }

    public void setExpectingLocation(boolean exception_location) {
        this.expecting_location = exception_location;
    }
    public void subscribeToLocationUpdate() {
        if (location_manager != null && myLocationListener != null) {
            double DESIRED_ACCURACY = 0;
            long MINIMAL_TIME = 1;
            double MINIMAL_DISTANCE = 0;
            boolean USE_IN_BACKGROUND = true;
            location_manager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, myLocationListener);
        }
    }

    public LocationListener getMyLocationListener() {
        return myLocationListener;
    }
    public CameraListener getMyCameraListener() {
        return myCameraListener;
    }
    public boolean isExpectingLocation() {
        return expecting_location;
    }
    public Point getMyLocation() {
        return myLocation;
    }

    public boolean isDrawable() {
        return is_drawable;
    }
    public void setDrawable(boolean is_drawable) {
        this.is_drawable = is_drawable;
    }

    public boolean isWalkable() {
        return is_walkable;
    }
    public void setWalkable(boolean is_walkable) {
        this.is_walkable = is_walkable;
    }

}
