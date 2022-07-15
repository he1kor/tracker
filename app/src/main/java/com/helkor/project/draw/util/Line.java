package com.helkor.project.draw.util;

import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;

public class Line {
    public static double length(Point first_point, Point second_point){
        return Geo.distance(first_point,second_point);
    }
    public static double steps(Point first_point, Point second_point, double step_length){
        return length(first_point,second_point) / step_length;
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