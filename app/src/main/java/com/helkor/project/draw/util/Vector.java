package com.helkor.project.draw.util;

import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObjectCollection;

public class Vector {

    private double length;
    private Point main_point;
    private Point second_point;

    public Vector(Point main_point, Point second_point){
        length = Vector.length(main_point,second_point);
        this.main_point = main_point;
        this.second_point = second_point;
    }

    public double length (Vector vector){
        return length;
    }
    public Point getMainPoint(){
        return main_point;
    }
    public Point getSecondPoint(){
        return second_point;
    }
    public static double length(Point first_point, Point second_point){
        return Geo.distance(first_point,second_point);
    }
    public static double steps(Point first_point, Point second_point, double step_length){
        return length(first_point,second_point) / step_length;
    }
    public Vector multiply(double multiplier, boolean is_from_main_point){

        double pointed_difference_latitude = second_point.getLatitude() - main_point.getLatitude();
        double pointed_difference_longitude = second_point.getLongitude() - main_point.getLongitude();

        Point shifted_point = is_from_main_point ?
                new Point(main_point.getLatitude() + (pointed_difference_latitude * multiplier),main_point.getLongitude() + (pointed_difference_longitude * multiplier))
                : new Point(second_point.getLatitude() - (pointed_difference_latitude * multiplier),second_point.getLongitude() - (pointed_difference_longitude * multiplier));

        Vector product = is_from_main_point ?
                new Vector(main_point,shifted_point)
                : new Vector(second_point,shifted_point);

        length = Vector.length(main_point,second_point);
        return product;
    }
    public Point getPointByMultiplier(double multiplier){
        Point intermediate_point = multiply(multiplier,true).getSecondPoint();
        return intermediate_point;
    }

    public static double toLongitudeStep(Point first_point, Point second_point, double step_length){
        double first_longitude = first_point.getLongitude();
        double second_longitude = second_point.getLongitude();

        double longitude_signed_distance = second_longitude - first_longitude;
        double steps = steps(first_point, second_point, step_length);

        return longitude_signed_distance / steps;
    }

    public static double toLatitudeStep(Point first_point, Point second_point, double step_length){
        double first_latitude = first_point.getLatitude();
        double second_latitude = second_point.getLatitude();

        double latitude_signed_distance = second_latitude - first_latitude;
        double steps = steps(first_point, second_point, step_length);

        return latitude_signed_distance / steps;
    }
}