package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import com.helkor.project.draw.util.Vector;
import com.helkor.project.draw.util.RouteSegment;
import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;

public class LineDrawer {

    private final Controller controller;
    private final MapState map_state;

    private ArrayList<RouteSegment> route;
    private MapObjectCollection map_object_collection;
    private Point last_point;
    private double path;
    private double division_step;
    private double min_real_step;
    private int travelled_index;
    private double travelled_path;

    boolean is_last_segment_divided;
    private boolean is_phantom_last;


    private final int MAX_POINTS = 400;
    private final int MAX_DIVISION_LINE = 500;
    private final double MIN_UNSEEN_STEP = 0.00000001;
    private static final double WALKED_ACCURACY = 100;

    @SuppressLint("HandlerLeak")
    public LineDrawer(Controller controller, MapState map_state) {
        this.controller = controller;
        route = new ArrayList<>();
        is_phantom_last = false;
        is_last_segment_divided = true;
        travelled_index = -1;
        travelled_path = 0;
        path = 0;
        this.map_state = map_state;
        map_object_collection = map_state.getMapView().getMap().getMapObjects().addCollection();
    }

    private void updateViewValues(){
        colorize();
        controller.updatePathValue(path,travelled_path);
    }
    private void updateTravelledPath(){
        travelled_path = 0;
        for (int i = 0; i < travelled_index; i++){
            travelled_path += route.get(i).getLength();
        }
        if (travelled_path == path){
            controller.setFinishedMode();
        }
        updateViewValues();
    }

    private void updateDrawnPath(){
        while (route.size() > MAX_POINTS) {
            removePoint(0,false);
        }
        updateViewValues();
    }

    private void drawNew(Point back_point, Point front_point){
        route.add(new RouteSegment(back_point,front_point,map_object_collection));
        path += route.get(route.size()-1).getLength();
        updateDrawnPath();
    }

    private void insertPoint(int inserting_index, Point point){
        removePoint(inserting_index);
        route.add(inserting_index,new RouteSegment(route.get(inserting_index-1).getLastPoint(),point,map_object_collection));
        path += route.get(inserting_index).getLength();

        route.add(inserting_index+1,new RouteSegment(point,route.get(inserting_index+1).getPreviousPoint(),map_object_collection));
        path += route.get(inserting_index+1).getLength();
        updateDrawnPath();
    }

    private void join(int index){
        //joins segment with the previous one
        RouteSegment replaced_segment = new RouteSegment(route.get(index-1).getLastPoint(),route.get(index).getPreviousPoint(),map_object_collection);

        removePoint(index-1);
        removePoint(index-1);

        route.add(index-1,replaced_segment);
        path += route.get(index-1).getLength();
        updateDrawnPath();
    }

    private void colorize(){
        double current_path = 0;
        if (route.size() > 0) {
            for (int i = 0; i < travelled_index; i++) {
                current_path += route.get(i).getLength();
                PolylineMapObject polyline_object = route.get(i).getPolylineObject();
                polyline_object.setStrokeColor(
                        Color.argb((int) Math.round(100 + (100 * current_path / path)),
                        50,
                        (int) Math.round(128 + (127 * current_path / path)),
                        (int) Math.round(70 + (128 * current_path / path)))
                );
            }
        }
        if (travelled_index < route.size()-1) {
            for (int i = travelled_index + 1; i < route.size(); i++) {
                current_path += route.get(i).getLength();
                PolylineMapObject polyline_object = route.get(i).getPolylineObject();
                polyline_object.setStrokeColor(
                        Color.argb((int) Math.round(100 + (100 * current_path / path)),
                        (int) Math.round(255 * current_path / path),
                        5,
                        155)
                );
            }
        }
    }
    private void removePoint(int index, boolean is_required_update_path){
        map_object_collection.remove(route.get(index).getPolylineObject());
        path -= route.get(index).getLength();
        route.remove(index);

        if (route.size() == 0) {
            last_point = null;
        }
        else if (index > route.size()-1){
            last_point = route.get(route.size()-1).getLastPoint();
        }
        if (is_required_update_path) {
            updateDrawnPath();
        }
    }

    private void removePoint(int index){
        removePoint(index,true);
    }
    public void addPoint(Point point, boolean is_phantom){
        Point front_point;
        if (is_phantom_last) removePoint(route.size()-1);
        is_phantom_last = is_phantom;
        if (last_point == null){
            last_point = point;
            front_point = new Point(
                    point.getLatitude() + MIN_UNSEEN_STEP,
                    point.getLongitude() + MIN_UNSEEN_STEP);
            drawNew(last_point,front_point);
            last_point = front_point;
            return;
        }
        double distance = Vector.length(point,last_point);
        double max_iterations = MAX_DIVISION_LINE / division_step;
        double iterations = Vector.steps(point,last_point,division_step);
        double longitude_step = Vector.toLongitudeStep(last_point,point,division_step);
        double latitude_step = Vector.toLatitudeStep(last_point,point,division_step);

        if (iterations > 1) {
            for (int i = 0; i < Math.floor(iterations) && i < max_iterations; i++) {
                front_point = new Point(
                        last_point.getLatitude() + latitude_step,
                        last_point.getLongitude() + longitude_step);
                drawNew(last_point,front_point);
                last_point = front_point;
            }
        }
        else if (distance > min_real_step) {
            front_point = point;
            drawNew(last_point,front_point);
            last_point = front_point;
        }
    }
    public void addPoint(Point point){
        addPoint(point,false);
    }

    private void remove_divided_segment(int last_travelled_index){
        if (is_last_segment_divided) {
            is_last_segment_divided = false;
            removePoint(last_travelled_index);
        }
    }
    public void checkForTravelled(Point current_position, double accuracy) {
        double checking_distance = accuracy + 2;
        //TODO: add smooth filling color
        int possible_indexes_amount = (int) Math.min(WALKED_ACCURACY, route.size()-travelled_index-1);
        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "checkForTravelled: ");
        for (int i = 0; (i < possible_indexes_amount); i++) {
            if (travelled_index == -1) {
                if (Geo.distance(current_position, route.get(0).getLastPoint()) < checking_distance) {
                    remove_divided_segment(travelled_index);
                    travelled_index++;
                }
            }
            else {
                //TODO: check meaning of accuracy in Mapkit and change formula
                if (Geo.distance(current_position, route.get(travelled_index).getLastPoint()) < checking_distance) {
                    remove_divided_segment(travelled_index);
                    travelled_index++;
                }
            }
        }

        //while (Geo.distance(current_position,route.get(travelled_index).getPreviousPoint()) < checking_distance){
//
        //}

        updateTravelledPath();
    }
    public void clear(){
        map_object_collection.clear();
        map_object_collection = map_state.getMap().getMapObjects().addCollection();
        route.clear();
        path = 0;
        is_phantom_last = false;
        travelled_index = -1;
        travelled_path = 0;
        updateDrawnPath();
        last_point = null;
    }
    public void resetWalkedPath(){
        travelled_index = -1;
        travelled_path = 0;
        updateViewValues();

    }
    public void setMinRealStep(double min_real_step){
        this.min_real_step = min_real_step;
    }

    public Point getLastPoint() {
        if (route.size() > 0) return route.get(route.size()-1).getLastPoint();
        else return null;
    }
    public double getPath() {
        return path;
    }

    public void setDivisionStep(double division_step) {
        this.division_step = division_step;
    }

}
