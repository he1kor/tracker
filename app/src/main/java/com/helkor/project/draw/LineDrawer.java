package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.widget.Toast;

import com.helkor.project.MainActivity;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LineDrawer {
    private PolylineMapObject polyline;
    private ArrayList<PolylineMapObject> polylineObjects = new ArrayList<>();
    private MapObjectCollection mapObjects;
    private Point point;
    private Point past_point;
    private ArrayList<ArrayList<Point>> points = new ArrayList<>();
    ArrayList<Double> distances = new ArrayList<Double>();
    MainActivity activity;
    private final int MAX_POINTS = 300;
    private final int MAX_DIVISION_LINE = 500;
    private final double DIVISION_STEP = 25.0;
    private final double MIN_UNSEEN_STEP = 0.00000001;
    private double min_real_step;
    private double distance;
    private double path = 0;
    private boolean is_counting = false;
    String text = "";

    @SuppressLint("HandlerLeak")
    public LineDrawer(MapView mapview,MainActivity activity) {
        this.activity = activity;
        mapObjects = mapview.getMap().getMapObjects().addCollection();
    }

    private void update(){
        distances.add(distance);
        path += distance;

        ArrayList<Point> last_points = new ArrayList<Point>();
            last_points.add(point);
            last_points.add(past_point);
        points.add(last_points);
        Polyline last_line = new Polyline(last_points);
        polylineObjects.add(polyline = mapObjects.addPolyline(last_line));
        if (polylineObjects.size() > MAX_POINTS) {
            mapObjects.remove(polylineObjects.get(0));
            polylineObjects.remove(polylineObjects.get(0));
            path -= distances.get(0);
            distances.remove(0);
        }
        //debugPolylines();
    }
    private void debugPolylines(){
        //String text = "";
        //for (ArrayList<Point> points_pair : points) {
        //    text = text + "\n" + points_pair.get(0).getLongitude() + " " + points_pair.get(0).getLatitude() + " | " + points_pair.get(1).getLongitude() + " " + points_pair.get(1).getLatitude();
        //}
        //if (polylineObjects != null && distances.size() > 0) text = "PolylineOBjects size: " + polylineObjects.size() + "\nPath: " + path + "\nFirst Distance: " + distances.get(0) + "\nLast Distance: " + distances.get(polylineObjects.size()-1);
        //MainActivity.test_text.setText(text);
    };
    private void colorize(){
        debugPolylines();
        double current_path = 0;
        for (int i = 0; i < polylineObjects.size(); i++) {
            current_path += distances.get(i);
            PolylineMapObject polyline = polylineObjects.get(i);
            polyline.setStrokeColor(Color.argb((int) Math.round(100 + (100*current_path/path)),(int) Math.round(255*current_path/path),5,155));
        }
    }
    public void addPoint(Point point){
        is_counting = true;
        if (this.point != null) {
            distance = Geo.distance(point,this.point);
            double first_longitude = this.point.getLongitude();
            double first_latitude = this.point.getLatitude();

            double second_longitude = point.getLongitude();
            double second_latitude = point.getLatitude();

            double longitude_signed_distance = second_longitude - first_longitude;
            double latitude_signed_distance = second_latitude - first_latitude;

            double iterations = distance / DIVISION_STEP;

            double longitude_step = longitude_signed_distance / iterations;
            double latitude_step = latitude_signed_distance / iterations;
            if (iterations > 1) {

                for (int i = 0; i < Math.floor(iterations) && i < MAX_DIVISION_LINE / DIVISION_STEP; i++) {
                    past_point = this.point;
                    this.point = new Point(past_point.getLatitude() + latitude_step, past_point.getLongitude() + longitude_step);
                    distance = Geo.distance(this.point,past_point);
                    update();
                }

            }
            else if (distance > min_real_step) {
                past_point = this.point;
                this.point = point;
                update();
            }
        }
        else{
            past_point = new Point(point.getLatitude() + MIN_UNSEEN_STEP, point.getLongitude() + MIN_UNSEEN_STEP);
            this.point = point;
            distance = Geo.distance(past_point,point);
            update();
        }
        colorize();
        is_counting = false;
    }
    public void clear(MapView mapview){
        mapObjects.clear();
        polylineObjects.clear();
        mapObjects = mapview.getMap().getMapObjects().addCollection();
        distances.clear();
        points.clear();
        path = 0;
        debugPolylines();
        point = null;
        past_point = null;
        text = "";
    }
    public boolean isCounting(){
        return is_counting;
    }
    public void setMinRealStep(double min_real_step){
        this.min_real_step = min_real_step;
    }
}
