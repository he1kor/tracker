package com.helkor.project.draw.util;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class Route {

    List<Point> routePoints;
    private double path;

    private double division_step;
    int max_iterations;

    private final double MIN_UNSEEN_STEP = 0.00000001;
    private double max_remove_distance;

    public Route(){
        path = 0;
        routePoints = new ArrayList<>();
    }

    private void calculatePath(){
        for (int i = 0; i < routePoints.size()-1; i++) {
            path += getDistance(i,i+1);
        }
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
            return;
        }
        path -= getDistance(0,1);
        routePoints.remove(0);
    }
    public void removeFrontPoint(){
        if (routePoints.size() == 0) {
            Log.w(TAG, "removeLastPoint: ", new IndexOutOfBoundsException());
            return;
        }
        if (routePoints.size() == 1) {
            routePoints.remove(0);
            return;
        }
        int index = routePoints.size()-1;
        path -= getDistance(index,index-1);
        routePoints.remove(index);
    }




    public void removeOverlimitedPoints(int limit){
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

        for (int i = 0; i < Math.floor(iterations) && i < max_iterations; i++) {
            Point new_point = new Point(
                    routePoints.get(routePoints.size()-1).getLatitude() + latitude_step,
                    routePoints.get(routePoints.size()-1).getLongitude() + longitude_step);
            addFrontPoint(new_point);
        }
    }
    public void buildToPoint(Point point, boolean tryRemove){
        if (routePoints.size() == 0){
            addFirstPoint(point);
            return;
        }
        Point last_point = getLastPoint();
        if (getDistance(point,last_point) <= max_remove_distance && size() >= 3 && tryRemove){
            removeFrontPoint();
            last_point = getLastPoint();
        }
        double distance = getDistance(point,last_point);
        if (distance > division_step){
            addPointsLine(point,last_point);
        }
    }




    public void setRoutePoints(List<Point> points){
        routePoints = points;
        calculatePath();
    }

    public void clear(){
        path = 0;
        routePoints.clear();
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
    public double getPath() {
        return path;
    }
    public List<Point> getPoints() {
        return routePoints;
    }
    public double getDistance(int i1, int i2){
        return Geo.distance(routePoints.get(i1),routePoints.get(i2));
    }
    protected double getDistance(Point point1, Point point2){
        return Geo.distance(point1,point2);
    }

}
