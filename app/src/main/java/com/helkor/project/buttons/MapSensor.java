package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.helkor.project.LineDrawer;
import com.helkor.project.MainActivity;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.geometry.geo.Projection;
import com.yandex.mapkit.geometry.geo.Projections;
import com.yandex.mapkit.geometry.geo.XYPoint;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.VisibleRegion;
import com.yandex.mapkit.mapview.MapView;

public class MapSensor{
    int test = 0;
    private float x;
    private float y;
    private Point point = new Point(0,0);
    CameraPosition currentCameraPosition;

    public MapSensor(MainActivity activity, View view, MapView mapView, LineDrawer lineDrawer) {
        Listener(activity, view, mapView, lineDrawer);
    }
    @SuppressLint("ClickableViewAccessibility")
    void Listener(MainActivity activity, View view, MapView mapView, LineDrawer lineDrawer){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                point = mapView.screenToWorld(new ScreenPoint(x,y));
                currentCameraPosition = mapView.getMap().getCameraPosition();
                lineDrawer.addPoint(point);;
                return true;
            }
        });
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    public Point getPoint(int COMFORTABLE_ZOOM_LEVEL){
        return point;
    }
}
