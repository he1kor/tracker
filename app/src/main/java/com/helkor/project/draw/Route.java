package com.helkor.project.draw;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.helkor.project.draw.util.Vector;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class Route {

    ArrayList<Point> routePoints;
    private double path;
    private double travelled_path;
    private int travelled_index;


    private double intermediate_multiplier;
    private boolean is_last_segment_divided;

    private double division_step;
    int max_iterations;

    private final double MIN_UNSEEN_STEP = 0.00000001;
    private final double WALKED_ACCURACY = 100;
    private final double SUBSTEP_PERCENT = 0.02;
    private double max_remove_distance;

    public Route(){
        path = 0;
        travelled_path = 0;
        travelled_index = 0;
        intermediate_multiplier = 0;
        is_last_segment_divided = false;
        routePoints = new ArrayList<>();
    }


    public void setDivisionStep(int division_step,int limit_points) {
        this.division_step = division_step;
        max_iterations = limit_points;
        max_remove_distance = ((double) division_step) / 1.5;
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
    protected void removeFrontPoint(){
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




    protected void removeOverlimitedPoints(int limit){
        while (routePoints.size() > limit) {
            removeRearPoint();
        }
    }
    private void addFirstPoint(Point point){
        Point similar_point = new Point(
                point.getLatitude() + MIN_UNSEEN_STEP,
                point.getLongitude() + MIN_UNSEEN_STEP);
        addFrontPoint(point);
        addFrontPoint(similar_point);
    }
    private void addPointsLine(Point point,Point last_point){
        double iterations = Vector.steps(last_point, point, division_step);
        double longitude_step = Vector.toLongitudeStep(last_point, point, division_step);
        double latitude_step = Vector.toLatitudeStep(last_point, point, division_step);

        System.out.println(Math.floor(iterations));
        for (int i = 0; i < Math.floor(iterations) && i < max_iterations; i++) {
            Point new_point = new Point(
                    routePoints.get(routePoints.size()-1).getLatitude() + latitude_step,
                    routePoints.get(routePoints.size()-1).getLongitude() + longitude_step);
            addFrontPoint(new_point);
        }
    }
    protected void buildToPoint(Point point){
        if (routePoints.size() == 0){
            addFirstPoint(point);
            System.out.println("first " + size());
            return;
        }
        Point last_point = getLastPoint();
        if (getDistance(point,last_point) <= max_remove_distance && size() >= 3){
            removeFrontPoint();
            last_point = getLastPoint();
        }
        double distance = getDistance(point,last_point);
        if (distance > division_step){
            addPointsLine(point,last_point);
        }
        System.out.println(size());
    }



    private void removeDividedPoint(){
        routePoints.remove(travelled_index);
        intermediate_multiplier = 0;
        is_last_segment_divided = false;
    }

    private void travelWholeStep(){
        travelled_path += getDistance(travelled_index,travelled_index+1);
        if (is_last_segment_divided) {
            removeDividedPoint();
        } else {
            travelled_index++;
        }
    }
    private void travelSubStep(Point intermediate_point){
        travelled_path += getDistance(routePoints.get(travelled_index),intermediate_point);

        if (is_last_segment_divided) {
            routePoints.remove(travelled_index);
        } else {
            travelled_index++;
        }

        routePoints.add(travelled_index,intermediate_point);
        is_last_segment_divided = true;
    }


    protected void checkWholeSteps(Point current_position,double checking_distance){
        int possible_indexes_amount = (int) Math.min(WALKED_ACCURACY, routePoints.size()-travelled_index-1);
        for (int i = 0; (i < possible_indexes_amount); i++) {
            if (getDistance(current_position, routePoints.get(travelled_index+1)) < checking_distance) {
                travelWholeStep();
            }
        }
    }
    protected void checkSubSteps(Point current_position,double checking_distance){
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
            travelSubStep(intermediate_point);
        }
    }


    protected void clear(){
        resetTravelledPath();
        path = 0;
        routePoints.clear();
    }
    protected void resetTravelledPath() {
        travelled_index = 0;
        travelled_path = 0;
        if (is_last_segment_divided) {
            routePoints.remove(travelled_index);
            is_last_segment_divided = false;
            intermediate_multiplier = 0;
        }
    }

    public Point getLastPoint() {
        if (routePoints.size() > 0) {
            return routePoints.get(routePoints.size()-1);
        } else {
            return null;
        }
    }
    public int size(){
        return routePoints.size();
    }
    public double getTravelledPath() {
        return travelled_path;
    }
    public double getPath() {
        return path;
    }
    public List<Point> getPoints() {
        return routePoints;
    }
    public int getTravelledIndex() {
        return travelled_index;
    }
    protected double getDistance(int i1, int i2){
        return Geo.distance(routePoints.get(i1),routePoints.get(i2));
    }
    private double getDistance(Point point1, Point point2){
        return Geo.distance(point1,point2);
    }

}
