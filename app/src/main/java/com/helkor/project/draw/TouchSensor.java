package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class TouchSensor {
    private final Controller controller;

    private float x;
    private float y;
    private boolean isStarted;

    private View drawable_relative;
    private MapView map_view;
    private LineDrawer line_drawer;

    private Point point = new Point(0,0);
    CameraPosition currentCameraPosition;

    public void setStarted(boolean started) {
        isStarted = started;
    }
    public TouchSensor(Controller controller, int id, MapState map_state, LineDrawer line_drawer) {

        this.controller = controller;
        Activity activity = controller.getActivity();

        this.drawable_relative = activity.findViewById(id);
        drawable_relative.setVisibility(View.INVISIBLE); 
        this.map_view = map_state.getMapView();
        this.line_drawer = line_drawer;
        Listener();
    }
    @SuppressLint("ClickableViewAccessibility")
    void Listener(){
        drawable_relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                point = map_view.screenToWorld(new ScreenPoint(x,y));
                currentCameraPosition = map_view.getMap().getCameraPosition();
                line_drawer.addPoint(point);
                return true;
            }
        });
    }
    public void show(){
        drawable_relative.setVisibility(View.VISIBLE);
    }
    public void hide(){
        drawable_relative.setVisibility(View.INVISIBLE);
    }
}
