package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.util.proto.ProtoOutputStream;

import com.helkor.project.R;
import com.helkor.project.draw.util.Vector;
import com.helkor.project.draw.util.RouteSegment;
import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.helkor.project.util.Intersection;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;

public class LineDrawer {

    private final Controller controller;
    private final MapState map_state;

    private MapObjectCollection map_object_collection;
    private ArrayList<RouteSegment> route;
    private ArrayList<RouteSegment> new_segments;
    private ArrayList<RouteSegment> removed_segments;
    private Point last_point;
    private double path;
    private double division_step;
    private double min_real_step;
    private int travelled_index;
    private double travelled_path;
    private double intermediate_multiplier;


    boolean is_last_segment_divided;
    boolean is_divided_segment_changed;
    private boolean is_phantom_last;

    private final int MAX_POINTS = 400;
    private final int MAX_DIVISION_LINE = 400;
    private final double MIN_UNSEEN_STEP = 0.00000001;
    private static final double WALKED_ACCURACY = 100;
    private RouteSegment specified_segment;

    @SuppressLint("HandlerLeak")
    public LineDrawer(Controller controller, MapState map_state) {
        this.controller = controller;
        route = new ArrayList<>();
        removed_segments = new ArrayList<>();
        new_segments = new ArrayList<>();
        is_phantom_last = false;
        is_last_segment_divided = true;
        travelled_index = -1;
        travelled_path = 0;
        path = 0;
        this.map_state = map_state;
        map_object_collection = map_state.getMapView().getMap().getMapObjects().addCollection();
    }
    private void updateMapObjectCollection(){
        route.removeAll(removed_segments);
        Pair<ArrayList<RouteSegment>,ArrayList<RouteSegment>> old_new_segments = Intersection.removeFromArrays(removed_segments,new_segments);
        removed_segments = old_new_segments.first;
        new_segments = old_new_segments.second;
        for (RouteSegment segment : new_segments){
            segment.setPolylineObject(map_object_collection.addPolyline(segment.getPolyline()));
        }
        for (RouteSegment segment : removed_segments){
            map_object_collection.remove(segment.getPolylineObject());
            route.remove(segment);
        }
        new_segments.clear();
        removed_segments.clear();
        colorize();
    }

    private void updateViewValues(){
        controller.updatePathValue(path,travelled_path);
    }
    private void updateTravelledPath(){
        travelled_path = 0;
        for (int i = 0; i <= travelled_index; i++){
            travelled_path += route.get(i).getLength();
        }
        if (travelled_path == path){
            controller.setFinishedMode();
        }
        updateViewValues();
    }

    private void updateDrawnPath(){
        while (route.size() > MAX_POINTS) {
            removeSegment(0,false);
        }
        updateViewValues();
    }

    private void drawNew(Point back_point, Point front_point){
        route.add(new RouteSegment(back_point,front_point,new_segments));
        path += route.get(route.size()-1).getLength();
        updateDrawnPath();
    }

    private void insertPoint(int inserting_index, Point central_point){
        Point previous_point = route.get(inserting_index).getPreviousPoint();
        Point last_point = route.get(inserting_index).getLastPoint();

        removeSegment(inserting_index,false);

        route.add(inserting_index,new RouteSegment(previous_point,central_point,new_segments));
        path += route.get(inserting_index).getLength();

        inserting_index++;
        route.add(inserting_index,new RouteSegment(central_point,last_point,new_segments));
        path += route.get(inserting_index).getLength();

        updateDrawnPath();
    }

    private void join(int index){
        //joins segment with the previous one
        RouteSegment replaced_segment = new RouteSegment(route.get(index-1).getPreviousPoint(),route.get(index).getLastPoint(),new_segments);

        removeSegment(index-1,false);
        removeSegment(index-1,false);

        route.add(index-1,replaced_segment);
        path += route.get(index-1).getLength();
        updateDrawnPath();
    }

    private void colorize(){
        double current_path = 0;
        for (int i = 0; i <= travelled_index; i++) {
            current_path += route.get(i).getLength();
            PolylineMapObject polyline_object = route.get(i).getPolylineObject();
            polyline_object.setStrokeColor(
                    Color.argb(
                    (int) Math.round(100 + (100 * current_path / path)),
                    50,
                    (int) Math.round(128 + (127 * current_path / path)),
                    (int) Math.round(70 + (128 * current_path / path)))
            );
        }
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
    private void removeSegment(int index, boolean is_required_update_path){
        removed_segments.add(route.get(index));
        path -= route.get(index).getLength();
        route.remove(index);

        if (is_required_update_path) {
            if (route.size() == 1) {
                last_point = null;
            }
            else if (index > route.size()-2){
                last_point = route.get(route.size()-2).getLastPoint();
            }
            updateDrawnPath();
        }
    }

    private void removeSegment(int index){
        removeSegment(index,true);
    }
    public void addPoint(Point point, boolean is_phantom){
        Point front_point;
        if (is_phantom_last) {
            removeSegment(route.size()-1);
        }
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
        updateMapObjectCollection();
    }
    public void addPoint(Point point){
        addPoint(point,false);
    }

    private void removeDividedSegment(int last_travelled_index){
        if (is_last_segment_divided) {
            is_last_segment_divided = false;
            intermediate_multiplier = 0;
            join(last_travelled_index+1);
            travelled_index--;
        }
    }
    public void checkForTravelled(Point current_position, double accuracy) {
        double checking_distance = accuracy + 2;
        int possible_indexes_amount = (int) Math.min(WALKED_ACCURACY, route.size()-travelled_index-1);
        for (int i = 0; (i < possible_indexes_amount); i++) {
            if (travelled_index == -1) {
                if (Geo.distance(current_position, route.get(0).getLastPoint()) < checking_distance) {
                    travelled_index++;
                    if (route.size() > 1) {
                        specified_segment = route.get(1);
                    }
                    intermediate_multiplier = 0;
                    is_divided_segment_changed = false;
                }
            }
            else {
                if (Geo.distance(current_position, route.get(travelled_index+1).getLastPoint()) < checking_distance) {
                    removeDividedSegment(travelled_index);
                    travelled_index++;
                }
            }
            updateMapObjectCollection();
        }
        updateTravelledPath();

        if (travelled_path < path && travelled_index >= 0) {
            if (!is_last_segment_divided) {
                specified_segment = route.get(travelled_index+1);
            }
            Point intermediate_point = RouteSegment.toVector(specified_segment).getPointByMultiplier(intermediate_multiplier);

            for (;(Geo.distance(current_position, intermediate_point) < checking_distance) && (intermediate_multiplier < 1);
                 intermediate_multiplier += 0.05) {
                is_divided_segment_changed = true;
                intermediate_point = RouteSegment.toVector(specified_segment).getPointByMultiplier(intermediate_multiplier);
            }
            if (is_divided_segment_changed){
                if (is_last_segment_divided) {
                    join(travelled_index+1);
                    travelled_index--;
                }
                travelled_index++;
                insertPoint(travelled_index,intermediate_point);
                is_last_segment_divided = true;
                is_divided_segment_changed = false;
            }
        }
        updateTravelledPath();
        updateMapObjectCollection();
    }
    public void clear(){
        map_object_collection.clear();
        route.clear();
        new_segments.clear();
        removed_segments.clear();

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
