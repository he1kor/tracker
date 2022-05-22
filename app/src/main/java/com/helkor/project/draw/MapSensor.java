package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class MapSensor{
    private float x;
    private float y;

    private View drawable_relative;
    private MapView mapView;
    private LineDrawer lineDrawer;

    private Point point = new Point(0,0);
    CameraPosition currentCameraPosition;

    public MapSensor(View drawable_relative, MapView mapView, LineDrawer lineDrawer) {
        this.drawable_relative = drawable_relative;
        this.mapView = mapView;
        this.lineDrawer = lineDrawer;
        Listener();
    }
    @SuppressLint("ClickableViewAccessibility")
    void Listener(){
        drawable_relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                point = mapView.screenToWorld(new ScreenPoint(x,y));
                currentCameraPosition = mapView.getMap().getCameraPosition();
                if (!lineDrawer.isCounting()) lineDrawer.addPoint(point);
                return true;
            }
        });
    }
}
