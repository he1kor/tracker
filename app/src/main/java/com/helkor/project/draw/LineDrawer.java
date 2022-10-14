package com.helkor.project.draw;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.util.proto.ProtoOutputStream;

import com.helkor.project.R;
import com.helkor.project.draw.util.Palette;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineDrawer {

    private final Controller controller;

    private MapObjectCollection map_object_collection;
    private PolylineMapObject route;
    private ArrayList<Point> routePoints;
    private double path;
    private double division_step;
    private double min_real_step;
    private int travelled_index;
    private double travelled_path;
    private double intermediate_multiplier;

    boolean is_last_segment_divided;
    boolean is_divided_segment_changed;
    private boolean is_temporary_last;

    private final int MAX_POINTS = 400;
    private final int MAX_SEGMENTS = MAX_POINTS - 1;
    private final int MAX_DIVISION_LINE = 400;
    private final double MIN_UNSEEN_STEP = 0.00000001;
    private final double SUBSTEP_PERCENT = 0.02;
    private final double MIN_ADDITIONAL_ACCURACY = 4;
    private static final double WALKED_ACCURACY = 100;
    private double max_iterations;

    //TODO: Bug when go to the common mode travelled path keep on
    @SuppressLint("HandlerLeak")
    public LineDrawer(Controller controller, MapState map_state) {
        this.controller = controller;
        map_object_collection = map_state.getMapView().getMap().getMapObjects().addCollection();
        routePoints = new ArrayList<>();

        is_temporary_last = false;
        is_last_segment_divided = true;
        travelled_index = 0;
        travelled_path = 0;
        path = 0;
    }
    private void checkIfFinished(){
        if (travelled_path == path){
            controller.setFinishedMode();
        }
    }
    private void update(){
        removeOverlimitedPoints();
        updateViewValues();
        if (routePoints.size() > 0) {
            if (route != null){
                route.setGeometry(new Polyline(routePoints));
            } else {
                route = map_object_collection.addPolyline(new Polyline(routePoints));
                Palette.configure(route,MAX_SEGMENTS);
            }
        }
        colorize();
    }

    private void updateViewValues(){
        controller.updatePathValue(path,travelled_path);
    }

    private void colorize(){
        if (routePoints.size() == 0){
            return;
        }
        double current_path = 0;
        ArrayList<Integer> colorIndexes = new ArrayList<>();
        for (int i = 1; i <= travelled_index; i++) {
            current_path += Geo.distance(routePoints.get(i-1),routePoints.get(i));
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_GREEN,MAX_SEGMENTS));
        }
        for (int i = travelled_index + 1; i < routePoints.size(); i++) {
            current_path += Geo.distance(routePoints.get(i-1),routePoints.get(i));
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_RED,MAX_SEGMENTS));
        }
        route.setStrokeColors(colorIndexes);
    }



    private void addFrontPoint(Point point){
        routePoints.add(point);
        if (routePoints.size() == 1) {
            return;
        }
        int index = routePoints.size()-1;
        path += Geo.distance(routePoints.get(index),routePoints.get(index-1));
    }
    private void removeRearPoint(){
        if (routePoints.size() == 0) {
            Log.w(TAG, "removeFirstPoint: ", new IndexOutOfBoundsException());
            return;
        }
        if (routePoints.size() == 1) {
            routePoints.remove(0);
            Log.i(TAG,"Removed most last point");
            return;
        }
        path -= Geo.distance(routePoints.get(0),routePoints.get(1));
        routePoints.remove(0);
    }
    private void removeFrontPoint(){
        if (routePoints.size() == 0) {
            Log.w(TAG, "removeLastPoint: ", new IndexOutOfBoundsException());
            return;
        }
        if (routePoints.size() == 1) {
            routePoints.remove(0);
            Log.i(TAG,"Removed most last point");
            return;
        }
        int index = routePoints.size()-1;
        path -= Geo.distance(routePoints.get(index),routePoints.get(index-1));
        routePoints.remove(index);
    }




    private void addFirstPoint(Point point){
        Point similar_point = new Point(
                point.getLatitude() + MIN_UNSEEN_STEP,
                point.getLongitude() + MIN_UNSEEN_STEP);
        addFrontPoint(point);
        addFrontPoint(similar_point);
    }
    private void removeOverlimitedPoints(){
        while (routePoints.size() > MAX_POINTS) {
            removeRearPoint();
        }
    }


    private void tryRemoveTemporaryPoint(){
        if (is_temporary_last) {
            removeFrontPoint();
        }
    }
    private void addPointsLine(Point point,Point last_point){
        double iterations = Vector.steps(last_point, point, division_step);
        System.out.println(iterations);
        double longitude_step = Vector.toLongitudeStep(last_point, point, division_step);
        double latitude_step = Vector.toLatitudeStep(last_point, point, division_step);

        for (int i = 0; i < Math.floor(iterations) && i < max_iterations; i++) {
            Point new_point = new Point(
                    routePoints.get(routePoints.size()-1).getLatitude() + latitude_step,
                    routePoints.get(routePoints.size()-1).getLongitude() + longitude_step);
            addFrontPoint(new_point);
        }
    }




    public void buildToPoint(Point point, boolean is_temporary){
        tryRemoveTemporaryPoint();
        is_temporary_last = is_temporary;
        if (routePoints.size() == 0){
            addFirstPoint(point);
        } else {
            Point last_point = routePoints.get(routePoints.size()-1);
            double distance = Geo.distance(point, last_point);
            if (distance > division_step){
                addPointsLine(point,last_point);
            }
            if (distance > min_real_step) {
                addFrontPoint(point);
            }
        }
        update();
    }
    public void buildToPoint(Point point){
        buildToPoint(point,false);
    }




    public void checkForTravelled(Point current_position, double accuracy) {
        double checking_distance = accuracy + MIN_ADDITIONAL_ACCURACY;
        checkWholeSteps(current_position,checking_distance);
        checkSubSteps(current_position,checking_distance);
        update();
        checkIfFinished();
    }
    public void checkWholeSteps(Point current_position,double checking_distance){
        int possible_indexes_amount = (int) Math.min(WALKED_ACCURACY, routePoints.size()-travelled_index-1);
        for (int i = 0; (i < possible_indexes_amount); i++) {
            if (travelled_index == 0) {
                if (Geo.distance(current_position, routePoints.get(1)) < checking_distance) {
                    travelled_index++;
                    travelled_path += Geo.distance(routePoints.get(travelled_index),routePoints.get(travelled_index-1));
                    intermediate_multiplier = 0;
                    is_divided_segment_changed = false;
                }
            }
            else {
                if (Geo.distance(current_position, routePoints.get(travelled_index+1)) < checking_distance) {
                    if (is_last_segment_divided) {
                        travelled_path += Geo.distance(routePoints.get(travelled_index),routePoints.get(travelled_index+1));
                        routePoints.remove(travelled_index);
                        is_last_segment_divided = false;
                        System.out.println("removed by whole");
                    } else {
                        travelled_index++;
                        travelled_path += Geo.distance(routePoints.get(travelled_index),routePoints.get(travelled_index-1));
                    }
                }
            }
        }
    }
    public void checkSubSteps(Point current_position,double checking_distance){
        if (travelled_path < path && travelled_index >= 0) {
            Point intermediate_point = new Vector(routePoints.get(travelled_index),routePoints.get(travelled_index+1)).getPointByMultiplier(intermediate_multiplier);

            for (;(Geo.distance(current_position, intermediate_point) < checking_distance) && (intermediate_multiplier < 1);
                 intermediate_multiplier += SUBSTEP_PERCENT) {
                is_divided_segment_changed = true;
                intermediate_point = new Vector(routePoints.get(travelled_index),routePoints.get(travelled_index+1)).getPointByMultiplier(intermediate_multiplier);
            }
            if (is_divided_segment_changed){
                if (is_last_segment_divided) {
                    travelled_path -= Geo.distance(routePoints.get(travelled_index),routePoints.get(travelled_index-1));
                    routePoints.remove(travelled_index);
                    System.out.println("removed by sub");
                } else {
                    travelled_index++;
                }
                routePoints.add(travelled_index,intermediate_point);
                travelled_path += Geo.distance(routePoints.get(travelled_index),routePoints.get(travelled_index-1));

                is_last_segment_divided = true;
                is_divided_segment_changed = false;
            }
        }
    }




    public void clear(){
        map_object_collection.clear();
        routePoints.clear();
        route = null;
        path = 0;
        is_temporary_last = false;
        travelled_index = 0;
        travelled_path = 0;
        update();
    }
    public void resetWalkedPath(){
        travelled_index = 0;
        travelled_path = 0;
        updateViewValues();
    }
    public void setMinRealStep(double min_real_step){
        this.min_real_step = min_real_step;
    }

    public Point getLastPoint() {
        if (routePoints.size() > 0) {
            return routePoints.get(routePoints.size()-1);
        } else {
            return null;
        }
    }
    public void setDivisionStep(double division_step) {
        this.division_step = division_step;
        max_iterations = MAX_DIVISION_LINE / division_step;
    }

}
