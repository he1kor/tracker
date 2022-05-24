package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;

import com.helkor.project.activities.MainActivity;
import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;
import java.util.Map;

public class LineDrawer {
    private final Controller controller;

    Activity activity;
    private PolylineMapObject polyline;
    private ArrayList<PolylineMapObject> polylineObjects = new ArrayList<>();
    private MapObjectCollection mapObjects;
    private Point point;
    private Point past_point;
    private ArrayList<ArrayList<Point>> points = new ArrayList<>();
    ArrayList<Double> distances = new ArrayList<Double>();
    private final int MAX_POINTS = 400;
    private final int MAX_DIVISION_LINE = 500;
    private final double MIN_UNSEEN_STEP = 0.00000001;
    private static final double WALKED_ACCURACY = 100;
    private double division_step;
    private double min_real_step;
    private double distance;
    private double travelled_path;

    private double path;
    private int travelled_index;
    private boolean is_counting = false;
    private boolean is_phantom_last = false;
    private MapState map_state;
    String text = "";

    @SuppressLint("HandlerLeak")
    public LineDrawer(Controller controller, MapState map_state) {
        this.controller = controller;
        activity = controller.getActivity();

        travelled_index = 0;
        travelled_path = 0;
        path = 0;
        this.map_state = map_state;
        mapObjects = map_state.getMapView().getMap().getMapObjects().addCollection();
    }

    private void update(){
        distances.add(distance);
        path += distance;
        controller.updatePathValue(path);
        ArrayList<Point> last_points = new ArrayList<Point>();
            last_points.add(point);
            last_points.add(past_point);
        points.add(last_points);
        Polyline last_line = new Polyline(last_points);
        polylineObjects.add(polyline = mapObjects.addPolyline(last_line));
        if (polylineObjects.size() > MAX_POINTS) removePoint(0);
    }
    private void colorize(){
        double current_path = 0;
        for (int i = 0; i < travelled_index; i++) {
            current_path += distances.get(i);
            PolylineMapObject polyline = polylineObjects.get(i);
            polyline.setStrokeColor(Color.argb((int) Math.round(100 + (100*current_path/path)),50,(int) Math.round(128+(127*current_path/path)),(int) Math.round(70+(128*current_path/path))));
        }
        for (int i = travelled_index; i < polylineObjects.size(); i++) {
            current_path += distances.get(i);
            PolylineMapObject polyline = polylineObjects.get(i);
            polyline.setStrokeColor(Color.argb((int) Math.round(100 + (100*current_path/path)),(int) Math.round(255*current_path/path),5,155));
        }
    }
    public void addPhantomPoint(Point point){
        if (is_phantom_last) removePoint(points.size()-1);
        is_phantom_last = true;
        is_counting = true;
        if (this.point != null) {
            distance = Geo.distance(point,this.point);
            double first_longitude = this.point.getLongitude();
            double first_latitude = this.point.getLatitude();

            double second_longitude = point.getLongitude();
            double second_latitude = point.getLatitude();

            double longitude_signed_distance = second_longitude - first_longitude;
            double latitude_signed_distance = second_latitude - first_latitude;

            double iterations = distance / division_step;

            double longitude_step = longitude_signed_distance / iterations;
            double latitude_step = latitude_signed_distance / iterations;
            if (iterations > 1) {

                for (int i = 0; i < Math.floor(iterations) && i < MAX_DIVISION_LINE / division_step; i++) {
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
    private void removePoint(int index){
        polyline = polylineObjects.get(index);
        mapObjects.remove(polyline);
        polylineObjects.remove(index);
        path -= distances.get(index);
        distances.remove(index);
        points.remove(index);

        if (points.size() > 0) point = points.get(points.size()-1).get(0);
        else point = null;
    }
    public void addPoint(Point point){
        if (is_phantom_last) removePoint(points.size()-1);
        is_phantom_last = false;
        is_counting = true;
        if (this.point != null) {
            distance = Geo.distance(point,this.point);
            double first_longitude = this.point.getLongitude();
            double first_latitude = this.point.getLatitude();

            double second_longitude = point.getLongitude();
            double second_latitude = point.getLatitude();

            double longitude_signed_distance = second_longitude - first_longitude;
            double latitude_signed_distance = second_latitude - first_latitude;

            double iterations = distance / division_step;

            double longitude_step = longitude_signed_distance / iterations;
            double latitude_step = latitude_signed_distance / iterations;
            if (iterations > 1) {

                for (int i = 0; i < Math.floor(iterations) && i < MAX_DIVISION_LINE / division_step; i++) {
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
    public void checkForTravelled(Point point, double accuracy) {
        double min_distance;

        double current_possible_distance = 0;
        int possible_indexes_amount = 0;
        for (int i = travelled_index; i < points.size();i++){
            current_possible_distance += distances.get(i);
            possible_indexes_amount++;
            if (possible_indexes_amount > WALKED_ACCURACY) break;
        }
        for (int i = travelled_index; i < travelled_index + possible_indexes_amount && i < points.size(); i++) {
            if (Geo.distance(point,points.get(travelled_index).get(0)) < WALKED_ACCURACY / 10 + accuracy) travelled_index = i;
        }
        travelled_path = 0;
        for (int i = 0; i < travelled_index; i++){
            travelled_path += distances.get(i);
        }
        controller.updatePathValue(path,travelled_path);
        colorize();
    }
    public void clear(){
        mapObjects.clear();
        polylineObjects.clear();
        mapObjects = map_state.getMap().getMapObjects().addCollection();
        distances.clear();
        points.clear();
        path = 0;
        travelled_index = 0;
        travelled_path = 0;
        controller.updatePathValue(path);
        point = null;
        past_point = null;
        text = "";
    }
    public void resetWalkedPath(){
        travelled_index = 0;
        travelled_path = 0;
        colorize();
        controller.updatePathValue(path,travelled_path);

    }
    public boolean isCounting(){
        return is_counting;
    }
    public void setMinRealStep(double min_real_step){
        this.min_real_step = min_real_step;
    }

    public Point getLastPoint() {
        if (points.size() > 0) return points.get(points.size()-1).get(0);
        else return null;
    }
    public double getPath() {
        return path;
    }

    public void setDivisionStep(double division_step) {
        this.division_step = division_step;
    }
}
