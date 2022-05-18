package com.helkor.project;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.helkor.project.tech.Bool;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;

public class LineDrawer {
    private PolylineMapObject polyline;
    private ArrayList<PolylineMapObject> polylineObjects = new ArrayList<>();
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
            else {
                last_points.add(point);
            }
        Polyline last_line = new Polyline(last_points);
        polylineObjects.add(polyline = mapObjects.addPolyline(last_line));
        polylineObjects.get(polylineObjects.size()-1).setStrokeColor(Color.BLACK);
        int length = 15;
        if (polylineObjects.size() > length) {
            mapObjects.remove(polylineObjects.get(polylineObjects.size() - length));
            polylineObjects.remove(polylineObjects.get(polylineObjects.size() - length));
        }
        debugPolylines();
    }
    void debugPolylines(){
        String text = "";
        for (PolylineMapObject debug_polyline : polylineObjects) {
            text = text + "1\n";
        }
        MainActivity.test_text.setText(text);
    };
    public void addPoint(Point point){
        if (this.point != null) past_point = this.point;
        this.point = point;
        update();
    }
    public void clear(MapView mapview){
        mapObjects.clear();
        polylineObjects.clear();
        mapObjects = mapview.getMap().getMapObjects().addCollection();
        debugPolylines();
        point = null;
        past_point = null;
    }

}
