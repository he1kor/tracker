package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.helkor.project.LineDrawer;
import com.helkor.project.MainActivity;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class MapSensor{
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
}
