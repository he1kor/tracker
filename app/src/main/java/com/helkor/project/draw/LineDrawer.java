package com.helkor.project.draw;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import com.helkor.project.draw.util.Palette;
import com.helkor.project.draw.util.Vector;
import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;

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

        intermediate_multiplier = 0;
        is_last_segment_divided = false;
        is_temporary_last = false;
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
            current_path += getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_GREEN,MAX_SEGMENTS));
        }
        for (int i = travelled_index + 1; i < routePoints.size(); i++) {
            current_path += getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_RED,MAX_SEGMENTS));
        }
        route.setStrokeColors(colorIndexes);
    }


    private double getDistance(int i1, int i2){
        return Geo.distance(routePoints.get(i1),routePoints.get(i2));
    }
    private double getDistance(Point point1, Point point2){
        return Geo.distance(point1,point2);
    }
    private void addFrontPoint(Point point){
        routePoints.add(point);
        if (routePoints.size() == 1) {
            return;
        }
        int index = routePoints.size()-1;
        path += getDistance(index,index-1);
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
        path -= getDistance(0,1);
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
        path -= getDistance(index,index-1);
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
            double distance = getDistance(point,last_point);
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


    private void removeDividedPoint(){
        routePoints.remove(travelled_index);
        intermediate_multiplier = 0;
        is_last_segment_divided = false;
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
            if (getDistance(current_position, routePoints.get(travelled_index+1)) < checking_distance) {
                travelled_path += getDistance(travelled_index,travelled_index+1);
                if (is_last_segment_divided) {
                    removeDividedPoint();
                } else {
                    travelled_index++;
                }
            }
        }
    }
    public void checkSubSteps(Point current_position,double checking_distance){
        if (travelled_index == routePoints.size()-1) {
            return;
        }

        Point intermediate_point = new Vector(routePoints.get(travelled_index),routePoints.get(travelled_index+1)).getPointByMultiplier(intermediate_multiplier);
        boolean is_divided_segment_changed = false;
        for (;(getDistance(current_position, intermediate_point) < checking_distance) && (intermediate_multiplier < 1);
        intermediate_multiplier += SUBSTEP_PERCENT) {
            is_divided_segment_changed = true;
            intermediate_point = new Vector(routePoints.get(travelled_index),routePoints.get(travelled_index+1)).getPointByMultiplier(intermediate_multiplier);
        }

        if (is_divided_segment_changed){
            travelled_path += getDistance(routePoints.get(travelled_index),intermediate_point);

            if (is_last_segment_divided) {
                routePoints.remove(travelled_index);
            } else {
                travelled_index++;
            }

            routePoints.add(travelled_index,intermediate_point);
            is_last_segment_divided = true;
        }
    }



    public void clear(){
        resetTravelledPath(false);
        map_object_collection.clear();
        routePoints.clear();
        route = null;
        path = 0;
        is_temporary_last = false;
        update();
    }
    public void resetTravelledPath(boolean requiredUpdate){
        travelled_index = 0;
        travelled_path = 0;
        if (is_last_segment_divided) {
            routePoints.remove(travelled_index);
            is_last_segment_divided = false;
        }
        if (requiredUpdate){
            update();
        }
    }
    public void resetTravelledPath(){
        resetTravelledPath(true);
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
