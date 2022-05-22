package com.helkor.project;

import android.util.Log;

import androidx.annotation.NonNull;

import com.helkor.project.draw.LineDrawer;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;

public class LocationUpdater {
    private CameraListener myCameraListener;
    private LocationListener myLocationListener;
    private MainActivity activity;
    private MapView map_view;
    private Map map;
    private LineDrawer line_drawer;

    private boolean is_drawable = false;
    private final int COMFORTABLE_ZOOM_LEVEL;

    private boolean expecting_location;
    private float expecting_zoom;

    private Point myLocation;
    LocationUpdater(MainActivity activity, MapView map_view, int COMFORTABLE_ZOOM_LEVEL, LineDrawer line_drawer){
        this.activity = activity;
        this.map_view = map_view;
        this.map = map_view.getMap();
        this.COMFORTABLE_ZOOM_LEVEL = COMFORTABLE_ZOOM_LEVEL;
        this.line_drawer = line_drawer;
        Listener();
    }

    void Listener() {
        myLocationListener = new LocationListener() {

            @Override
            public void onLocationUpdated(@NonNull Location location) {
                //moveCamera(activity, location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                if (myLocation == null) {
                    map.setMapType(MapType.VECTOR_MAP);
                    moveCamera(map_view,location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                }
                if (is_drawable && !line_drawer.isCounting()) line_drawer.addPoint(location.getPosition());

                myLocation = location.getPosition();
                if (expecting_location) {
                    moveCamera(map_view,location.getPosition(), expecting_zoom);
                    setExpectingLocation(false);
                }

                Log.w(activity.TAG, "my location - " + myLocation.getLatitude() + "," + myLocation.getLongitude());
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

    public void moveCamera(MapView map_view,Point point, float zoom) {
        if (point != null) {
            map.move(
                    new CameraPosition(point, zoom, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 1),
                    null
            );
        }
        else {
            expecting_zoom = zoom;
            expecting_location = true;
        }
    }

    public void setExpectingLocation(boolean exception_location) {
        this.expecting_location = exception_location;
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

}
