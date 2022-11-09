package com.helkor.project.draw;


import android.util.Log;

import androidx.annotation.NonNull;

import com.helkor.project.map.util.ShortLocationArray;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

public class LocationSensor{

    private final MarkDrawer markDrawer;
    private LocationListener location_listener;
    private final MapView map_view;
    private final MapKit map_kit;
    private Map map;
    private ShortLocationArray last_locations;
    private Point last_sent_point;
    private final int MAX_CHECK_LINE = 1;

    private boolean is_drawable = false;
    private boolean is_walkable = false;
    private final float COMFORTABLE_ZOOM_LEVEL;

    private UpdateListener update_listener;
    public interface UpdateListener{
        void onNewPoint(Point point, boolean isTemp);
        void onPointTravelled(Point point,double accuracy);
    }

    private CameraInitListener camera_init_listener;
    public interface CameraInitListener{
        void onCameraInitialized();
    }


    private boolean expecting_location;
    private float expecting_zoom;

    private LocationManager location_manager;

    private Location my_location;
    public LocationSensor(Object update_listener_implementation_context,Object camera_listener_implementation_context,MapView map_view, MapKit map_kit,float COMFORTABLE_ZOOM_LEVEL,MarkDrawer markDrawer){
        this.markDrawer = markDrawer;
        this.map_kit = map_kit;
        this.map_view = map_view;
        this.map = map_view.getMap();
        this.COMFORTABLE_ZOOM_LEVEL = COMFORTABLE_ZOOM_LEVEL;
        last_locations = new ShortLocationArray(MAX_CHECK_LINE);
        tryAddUpdateListener(update_listener_implementation_context);
        tryAddCameraInitListener(camera_listener_implementation_context);

        initializeLocationListener();
    }
    private void tryAddUpdateListener(Object implementation_context){
        try {
            update_listener = (UpdateListener) implementation_context;
        } catch (ClassCastException e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement UpdateListener");
        }
    }
    private void tryAddCameraInitListener(Object implementation_context){
        try {
            camera_init_listener = (CameraInitListener) implementation_context;
        } catch (ClassCastException e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement CameraInitListener");
        }
    }
    private void setLocation(Location location){
        last_locations.add(location);
    }

    private void initializeLocationListener() {
        location_manager = map_kit.createLocationManager();
        location_listener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                last_locations.add(location);

                if (is_walkable) {
                    update_listener.onPointTravelled(location.getPosition(),location.getAccuracy());
                }
                if (is_drawable && last_locations.size() == MAX_CHECK_LINE) {
                    addPoint();
                }
                if (my_location == null) {
                    firstMoveCamera(location.getPosition(), 16.25f);
                }
                if (expecting_location) {
                    moveCamera(location.getPosition(), expecting_zoom,1);
                    setExpectingLocation(false);
                }
                my_location = location;
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    Log.i("LocationListener: ","Location Status is not available.");
                }
            }
        };
        subscribeToLocationUpdate();
    }
    public void subscribeToLocationUpdate() {
        double DESIRED_ACCURACY = 0;
        long MINIMAL_TIME = 0;
        double MINIMAL_DISTANCE = 0.5;
        boolean USE_IN_BACKGROUND = true;
        location_manager.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME,
                MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, location_listener);
    }
    public void unSubscribeToLocationUpdate(){
        location_manager.unsubscribe(location_listener);
    }
    private void addPoint(){
        if (last_sent_point != null) {
            if (Geo.distance(last_sent_point, last_locations.getLastPoint()) > last_locations.getLastAccuracy())
                update_listener.onNewPoint(last_locations.getLastPoint(),false);
            else {
                update_listener.onNewPoint(last_locations.getLastPoint(),true);
            }
        }
        else {
            update_listener.onNewPoint(last_locations.getMinimalAccuracyPoint().getPoint(),false);
        }
        last_sent_point = last_locations.getLastPoint();
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
                        camera_init_listener.onCameraInitialized();
                    }
                }
        );
    }

    public void setExpectingLocation(boolean exception_location) {
        this.expecting_location = exception_location;
    }

    public LocationListener getLocationListener() {
        return location_listener;
    }
    public boolean isExpectingLocation() {
        return expecting_location;
    }
    public Location getMyLocation() {
        return my_location;
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
