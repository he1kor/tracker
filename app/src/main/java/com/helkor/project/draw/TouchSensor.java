package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;
import java.util.List;

public class TouchSensor {

    private float x;
    private float y;

    private final View drawable_relative;
    private final MapView map_view;

    public interface OnTouchListener{
        void onTouch(Point point,boolean isTemp);
    }
    public interface OnClickListener{
        void onClick(Point point);
    }
    List<OnTouchListener> onTouchListeners;
    List<OnClickListener> onClickListeners;

    private Point point = new Point(0,0);
    public TouchSensor(View drawable_relative, MapView map_view) {

        this.drawable_relative = drawable_relative;
        turnOff();
        this.map_view = map_view;
        onClickListeners = new ArrayList<>();
        onTouchListeners = new ArrayList<>();
        Listener();
    }
    public void addOnTouchListener(Object implementation_context){
        try {
            onTouchListeners.add((OnTouchListener) implementation_context);
        } catch (ClassCastException e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement OnTouchListener");
        }
    }
    public void addOnClickListener(Object implementation_context){
        try {
            onClickListeners.add((OnClickListener) implementation_context);
        } catch (ClassCastException e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement OnClickListener");
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
                TouchSensor.this.onTouch(point,true);
                if (event.getAction() == MotionEvent.ACTION_UP){
                    onClick(point);
                }
                return true;
            }
        });
    }
    private void onTouch(Point point, boolean isTemp){
        for (OnTouchListener listener : onTouchListeners) {
            listener.onTouch(point,isTemp);
        }
    }
    private void onClick(Point point){
        for (OnClickListener listener : onClickListeners) {
            listener.onClick(point);
        }
    }
    public void turnOn(){
        drawable_relative.setVisibility(View.VISIBLE);
    }
    public void turnOff(){
        drawable_relative.setVisibility(View.INVISIBLE);
    }
}
