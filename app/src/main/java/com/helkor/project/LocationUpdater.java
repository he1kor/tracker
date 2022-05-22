package com.helkor.project;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class LocationUpdater {
    private boolean expectingLocation;
    private CameraListener myCameraListener;
    LocationListener myLocationListener;

    private Point myLocation;
    LocationUpdater(MainActivity activity, int COMFORTABLE_ZOOM_LEVEL){
        Listener(activity, COMFORTABLE_ZOOM_LEVEL);
    }

    void Listener(MainActivity activity, int COMFORTABLE_ZOOM_LEVEL) {
        myLocationListener = new LocationListener() {

            @Override
            public void onLocationUpdated(@NonNull Location location) {
                //moveCamera(activity, location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                if (myLocation == null) {
                    activity.mapview.getMap().setMapType(MapType.VECTOR_MAP);
                    moveCamera(activity, location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                }

                myLocation = location.getPosition();
                if (expectingLocation == true) {
                    moveCamera(activity, location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
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

    public void moveCamera(MainActivity activity,Point point, float zoom) {
        activity.mapview.getMap().move(
                new CameraPosition(point,zoom,0.0f,0.0f),
                new Animation(Animation.Type.SMOOTH,1),
                null
        );
    }

    public void setExpectingLocation(boolean exceptionLocation) {
        this.expectingLocation = exceptionLocation;
    }


    public LocationListener getMyLocationListener() {
        return myLocationListener;
    }
    public CameraListener getMyCameraListener() {
        return myCameraListener;
    }
    public boolean isExpectingLocation() {
        return expectingLocation;
    }
    public Point getMyLocation() {
        return myLocation;
    }

}
