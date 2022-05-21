package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.widget.Toast;

import com.helkor.project.MainActivity;
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
    final int MAX_POINTS = 100;
    double distance;
    double path = 0;

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

        colorize();
        //debugPolylines();
    }
    private void debugPolylines(){
        String text = "";
        for (ArrayList<Point> points_pair : points) {
            text = text + "\n" + points_pair.get(0).getLongitude() + " " + points_pair.get(0).getLatitude() + " | " + points_pair.get(1).getLongitude() + " " + points_pair.get(1).getLatitude();
        }
        MainActivity.test_text.setText(text);
    };
    private void colorize(){
        double current_path = 0;
        for (int i = 0; i < polylineObjects.size(); i++) {
            current_path += distances.get(i);
            PolylineMapObject polyline = polylineObjects.get(i);
            polyline.setStrokeColor(Color.argb((int) Math.round(100 + (100*current_path/path)),(int) Math.round(255*current_path/path),5,155));
        }
    }
    public void addPoint(Point point){
        if (this.point != null) {
            distance = getDistance(point,this.point);
            double first_longitude = this.point.getLongitude();
            double first_latitude = this.point.getLatitude();

            double second_longitude = point.getLongitude();
            double second_latitude = point.getLatitude();

            double longitude_signed_distance = second_longitude - first_longitude;
            double latitude_signed_distance = second_latitude - first_latitude;

            double iterations = distance / 0.0002;

            double longitude_step = longitude_signed_distance / iterations;
            double latitude_step = latitude_signed_distance / iterations;
            if (iterations > 1) {

                for (int i = 0; i < Math.floor(iterations) && i < 10; i++) {
                    //if (i == Math.ceil(iterations) - 1) {
                    //    debugPolylines();
                    //    longitude_step = (Math.ceil(iterations)-iterations) * longitude_step;
                    //    latitude_step = (Math.ceil(iterations)-iterations) * latitude_step;
                    //}
                    past_point = this.point;
                    this.point = new Point(past_point.getLatitude() + latitude_step, past_point.getLongitude() + longitude_step);
                    update();
                }

            }
            else if (distance > 0.00002) {
                past_point = this.point;
                this.point = point;
                update();
            }
        }
        else{
            past_point = new Point(point.getLatitude() + 0.00000001, point.getLongitude() + 0.00000001);
            this.point = point;
            update();
        }
    }
    private double getDistance(Point first_point, Point second_point){
        double first_longitude = first_point.getLongitude();
        double first_latitude = first_point.getLatitude();

        double second_longitude = second_point.getLongitude();
        double second_latitude = second_point.getLatitude();

        double longitude_distance = Math.abs(second_longitude - first_longitude);
        double latitude_distance = Math.abs(second_latitude - first_latitude);

        double distance = Math.sqrt(Math.pow(longitude_distance,2) + Math.pow(latitude_distance,2));
        return distance;
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
    }

}
