package com.helkor.project;

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
    private Point point;
    private Point past_point;

    @SuppressLint("HandlerLeak")
    public LineDrawer(MapView mapview) {
        mapObjects = mapview.getMap().getMapObjects().addCollection();
    }
    void update(){
        ArrayList<Point> last_points = new ArrayList<Point>();
            last_points.add(point);
            if (past_point != null) {
                last_points.add(past_point);
            }
            else {last_points.add(point);}
        Polyline last_line = new Polyline(last_points);
        polyline = mapObjects.addPolyline(last_line);
    }
    public void addPoint(Point point){
        if (this.point != null) past_point = this.point;
        this.point = point;
        polylinePoints.add(point);
        update();
    }

}
