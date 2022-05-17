package com.helkor.project.buttons;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.helkor.project.MainActivity;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;

public class LineDrawer {
    private PolylineMapObject polyline;
    private ArrayList<Point> polylinePoints = new ArrayList<>();
    private MapObjectCollection mapObjects;
    static public Handler handler;
    private Point point;

    @SuppressLint("HandlerLeak")
    public LineDrawer(MapView mapview) {
        mapObjects = mapview.getMap().getMapObjects().addCollection();
        handler = new Handler() {   // создание хэндлера
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                MainActivity.test();
            }
        };
    }
    void update(){
        polyline = mapObjects.addPolyline(new Polyline(polylinePoints));
    }
    public void addPoint(Point point){
        this.point = point;
        polylinePoints.add(point);
        update();
    }

}
