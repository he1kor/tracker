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

    private float x;
    private float y;

    private final View drawable_relative;
    private final MapView map_view;

    public interface Listener{
        void onTouch(Point point,boolean isTemp);
    }
    Listener listener;

    private Point point = new Point(0,0);
    public TouchSensor(Object implementation_context,View drawable_relative, MapView map_view) {

        this.drawable_relative = drawable_relative;
        drawable_relative.setVisibility(View.INVISIBLE); 
        this.map_view = map_view;
        trySetListener(implementation_context);
        Listener();
    }
    private void trySetListener(Object implementation_context){
        try {
            listener = (Listener) implementation_context;
        } catch (ClassCastException e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement Listener");
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    void Listener(){
        drawable_relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                point = map_view.screenToWorld(new ScreenPoint(x,y));
                listener.onTouch(point,true);
                return true;
            }
        });
    }
    public void turnOn(){
        drawable_relative.setVisibility(View.VISIBLE);
    }
    public void turnOff(){
        drawable_relative.setVisibility(View.INVISIBLE);
    }
}
